package com.sofichallenge.transactionapi.dependency.impl

import com.sofichallenge.transactionapi.businessobject.{GetMerchantsSpecifiers, GetTransactionsSpecifiers, SearchTxsResultsBO, TransactionBO, UserBO}
import com.sofichallenge.transactionapi.database.tablemapping.Tables._
import com.sofichallenge.transactionapi.database.tablemapping.TablesWithDb
import com.sofichallenge.transactionapi.datetime.{DateTimeFactory, JsonDateFormats}
import com.sofichallenge.transactionapi.dependency.api.TransactionApi
import com.sofichallenge.transactionapi.handler.TransactionMagicConstants

import scala.concurrent.{ExecutionContext, Future}

/**
  * Transaction api impl that writes to database
  */
class TransactionApiDBImpl(val tablesWithDb: TablesWithDb)(implicit val ec: ExecutionContext) extends TransactionApi with nl.grons.metrics.scala.DefaultInstrumented {
  import tablesWithDb.profile.api._

  private val db = tablesWithDb.db

  private val txns = TableQuery[Transaction]

  override def createTransaction(tx: TransactionBO): Future[TransactionBO] = {
    val dbAction = for {
      _ <- txns += TransactionRow(0, tx.txId, tx.userId, tx.merchantId, tx.merchant, tx.price,
        DateTimeFactory.toTimeStamp(tx.purchaseDate), tx.void, DateTimeFactory.currentTimeStamp)
    } yield tx

    val namedAction = dbAction.named(s"${getClass.getSimpleName} - storeTx")

    val storing = metrics.timer(s"${getClass.getSimpleName} - storeTx")

    storing.timeFuture(db.run(namedAction))
  }

  override def updateTransaction(txId: Int, void: Boolean): Future[Option[TransactionBO]] = {
    val dbAction = (for {
      _ <- txns.filter(_.transactionId === txId).map(_.void).update(void)
      updatedRows <- txns.filter(_.transactionId === txId).result
      bos = toTransactionBOs(updatedRows)
    } yield bos.headOption).transactionally

    val namedAction = dbAction.named(s"${getClass.getSimpleName} - voidTx")

    val voiding = metrics.timer(s"${getClass.getSimpleName} - voidTx")

    voiding.timeFuture(db.run(namedAction))
  }

  override def getTransaction(txId: Int): Future[Option[TransactionBO]] = {
    val dbAction = txns.filter(_.transactionId === txId).take(1).result.map(toTransactionBOs).map(_.headOption)

    val namedAction = dbAction.named(s"${getClass.getSimpleName} - getTx")

    db.run(namedAction)
  }

  override def getTransactionCount(userIds: Seq[Int]): Future[Map[Int, Int]]= {
    val dbAction = txns.filter(_.userId inSet userIds).groupBy(_.userId).map {
      case (userId, transs) => userId -> transs.length
    }.result.map(_.toMap)

    val namedAction = dbAction.named(s"${getClass.getSimpleName} - getTranssCount")

    db.run(namedAction)
  }

  override def getMerchants(specs: GetMerchantsSpecifiers): Future[Seq[String]] = {
    val initQuery = txns.filter(_.userId inSet specs.userIds).groupBy(row => (row.merchantId, row.merchantName)).map {
      case ((merchantId, merchantName), transs) => (merchantId, merchantName) -> transs.length
    }

    val sortedQuery = initQuery.sortBy({
      case ((id, name), count) =>
        if (specs.sortAsc) count.asc else count.desc
    })

    val limitQuery = sortedQuery.take(specs.limit)

    val dbAction = limitQuery.result.map(_.map {
      case ((id, name), count) => name
    })

    val namedAction = dbAction.named(s"${getClass.getSimpleName} - getMerchants")

    db.run(namedAction)
  }

  override def getTransactions(specifiers: GetTransactionsSpecifiers): Future[SearchTxsResultsBO]= {
    val initialQuery = txns.filter(_.userId === specifiers.userId)
    val queryWithPagination = (specifiers.sortKey, specifiers.offsetInt, specifiers.offsetDate,
      specifiers.offsetBigDec) match {
      case (Some(TransactionMagicConstants.sortTxId), Some(txId), _, _) =>
        initialQuery.filter(row => {
          if (specifiers.forwardCutOffDirection) row.transactionId < txId else row.transactionId > txId
        })
      case (Some(TransactionMagicConstants.sortMerchantId), Some(merchantId), _, _) =>
        initialQuery.filter(row => {
          if (specifiers.forwardCutOffDirection) row.merchantId < merchantId else row.merchantId > merchantId
        })
      case (Some(TransactionMagicConstants.sortPurchaseDate), _, Some(purchaseDate), _) =>
        val offsetDate = DateTimeFactory.toTimeStamp(purchaseDate)
        initialQuery.filter(row => {
          if (specifiers.forwardCutOffDirection) row.purchaseDate < offsetDate else row.purchaseDate > offsetDate
        })
      case (Some(TransactionMagicConstants.sortPrice), _, _, Some(price)) =>
        initialQuery.filter(row => {
          if (specifiers.forwardCutOffDirection) row.price < price else row.price > price
        })
      case _ =>
        initialQuery
    }

    val sortedQuery = specifiers.sortKey match {
      case Some(TransactionMagicConstants.sortTxId) =>
        queryWithPagination.sortBy(row => {
          if (specifiers.forwardCutOffDirection) row.transactionId.desc else row.transactionId.asc
        })
      case Some(TransactionMagicConstants.sortMerchantId) =>
        queryWithPagination.sortBy(row => {
          if (specifiers.forwardCutOffDirection) row.merchantId.desc else row.merchantId.asc
        })
      case Some(TransactionMagicConstants.sortPurchaseDate) =>
        queryWithPagination.sortBy(row => {
          if (specifiers.forwardCutOffDirection) row.purchaseDate.desc else row.purchaseDate.asc
        })
      case Some(TransactionMagicConstants.sortPrice) =>
        queryWithPagination.sortBy(row => {
          if (specifiers.forwardCutOffDirection) row.price.desc else row.price.asc
        })
      case _ =>
        queryWithPagination
    }

    val dataAction = sortedQuery.take(specifiers.limit+1).result.map(rows => {
      val bos = toTransactionBOs(rows)
      val bosSorted = (if (specifiers.forwardCutOffDirection) bos else bos.reverse).take(specifiers.limit)
      val firstOption = bosSorted.headOption
      val lastOption = bosSorted.lastOption
      val (candNextOffset: Option[String], candPrevOffset: Option[String]) = specifiers.sortKey match {
        case Some(TransactionMagicConstants.sortTxId) =>
          (lastOption.map(_.txId.toString), firstOption.map(_.txId.toString))
        case Some(TransactionMagicConstants.sortMerchantId) =>
          (lastOption.map(_.merchantId.toString), firstOption.map(_.merchantId.toString))
        case Some(TransactionMagicConstants.sortPurchaseDate) =>
          (lastOption.map(bo => JsonDateFormats.dateTimeFormatter.print(bo.purchaseDate)),
            firstOption.map(bo => JsonDateFormats.dateTimeFormatter.print(bo.purchaseDate)))
        case Some(TransactionMagicConstants.sortPrice) =>
          (lastOption.map(_.price.toString), firstOption.map(_.price.toString))
        case _ =>
          (None, None)
      }
      val (nextOffset, prevOffset) =
        (bos.size > bosSorted.size, specifiers.offsetInt.isDefined || specifiers.offsetDate.isDefined ||
          specifiers.offsetBigDec.isDefined, specifiers.forwardCutOffDirection) match {
          case (true, true, _) =>
            (candNextOffset, candPrevOffset)
          case (false, true, true) =>
            (None, candPrevOffset)
          case (false, true, false) =>
            (candNextOffset, None)
          case (true, false, true) =>
            (candNextOffset, None)
          case (true, false, false) =>
            (None, candPrevOffset)
          case (false, false, _) =>
            (None, None)
          case _ =>
            (None, None)
        }

      (bosSorted, nextOffset, prevOffset)
    })

    for {
      (bos, nextOffset, prevOffset) <- db.run(dataAction)
      count <- db.run(sortedQuery.length.result)
    } yield SearchTxsResultsBO(bos, prevOffset, nextOffset, count)
  }

  private def toTransactionBOs(rows: Seq[TransactionRow]): Seq[TransactionBO] = {
    rows.map(row => TransactionBO(row.transactionId, row.userId, row.merchantId, row.merchantName, row.price,
      DateTimeFactory.toDateTimePacific(row.purchaseDate), row.void))
  }
}
