package com.sofichallenge.transactionapi.dependency.api

import com.sofichallenge.transactionapi.businessobject.{GetMerchantsSpecifiers, GetTransactionsSpecifiers, SearchTxsResultsBO, TransactionBO}

import scala.concurrent.Future

/**
  *
  */
trait TransactionApi {
  def createTransaction(trans: TransactionBO): Future[TransactionBO]

  def updateTransaction(txId: Int, void: Boolean): Future[Option[TransactionBO]]

  def getTransaction(txId: Int): Future[Option[TransactionBO]]

  def getTransactionCount(userIds: Seq[Int]): Future[Map[Int, Int]]

  def getMerchants(specs: GetMerchantsSpecifiers): Future[Seq[String]]

  def getTransactions(specifiers: GetTransactionsSpecifiers): Future[SearchTxsResultsBO]
}
