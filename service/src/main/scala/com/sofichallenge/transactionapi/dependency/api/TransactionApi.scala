package com.sofichallenge.transactionapi.dependency.api

import com.sofichallenge.transactionapi.businessobject.TransactionBO

import scala.concurrent.Future

/**
  *
  */
trait TransactionApi {
  def createTransaction(trans: TransactionBO): Future[TransactionBO]

  def updateTransaction(txId: String, void: Boolean): Future[Option[TransactionBO]]
}
