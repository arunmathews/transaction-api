package com.sofichallenge.transactionapi.dependency.impl

import com.sofichallenge.transactionapi.businessobject.{TransactionBO, UserBO}
import com.sofichallenge.transactionapi.database.tablemapping.Tables._
import com.sofichallenge.transactionapi.database.tablemapping.TablesWithDb
import com.sofichallenge.transactionapi.datetime.DateTimeFactory
import com.sofichallenge.transactionapi.dependency.api.TransactionApi

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
class TramsactionApiDBImpl(val tablesWithDb: TablesWithDb)(implicit val ec: ExecutionContext) extends TransactionApi {
  import tablesWithDb.profile.api._

  private val db = tablesWithDb.db

  private val txns = TableQuery[Transaction]

  override def createTransaction(tx: TransactionBO): Future[TransactionBO] = {
    val dbAction = for {
      _ <- txns += TransactionRow(0, tx.txId, tx.userId, tx.merchantId, tx.merchant, tx.price,
        DateTimeFactory.toTimeStamp(tx.purchaseDate), tx.void, DateTimeFactory.currentTimeStamp)
    } yield tx

    val namedAction = dbAction.named(s"${getClass.getSimpleName} - storeTx")

    db.run(namedAction)
  }

  override def updateTransaction(txId: String, void: Boolean): Future[Option[TransactionBO]] = {
    val dbAction = (for {
      _ <- txns.filter(_.transactionId === txId).map(_.void).update(void)
      updatedRows <- txns.filter(_.transactionId === txId).result
      bos = toTransactionBOs(updatedRows)
    } yield bos.headOption).transactionally

    val namedAction = dbAction.named(s"${getClass.getSimpleName} - voidTx")

    db.run(namedAction)
  }

  private def toTransactionBOs(rows: Seq[TransactionRow]): Seq[TransactionBO] = {
    rows.map(row => TransactionBO(row.transactionId, row.userId, row.merchantId, row.merchantName, row.price,
      DateTimeFactory.toDateTimePacific(row.purchaseDate), row.void))
  }
}
