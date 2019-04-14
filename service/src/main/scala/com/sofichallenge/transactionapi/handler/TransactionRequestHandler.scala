package com.sofichallenge.transactionapi.handler

import com.sofichallenge.transactionapi.businessobject.{MerchantBO, TransactionBO, UserBO}
import com.sofichallenge.transactionapi.dependency.api._
import scalaz._
import Scalaz._
import com.sofichallenge.transactionapi.service.validation.businessobject.BOValidationFailure
import com.sofichallenge.transactionapi.service.validation.businessobject.BOValidationFailureCode._
import com.sofichallenge.transactionapi.service.validation.businessobject.TransactionOperationFieldName._

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
class TransactionRequestHandler(transactionApi: TransactionApi)(implicit val ec: ExecutionContext) {
  def createTransaction(txBO: TransactionBO): Future[TransactionBO] = {
    transactionApi.createTransaction(txBO)
  }

  def voidTransaction(txId: String): Future[ValidationNel[BOValidationFailure, TransactionBO]] = {
    transactionApi.updateTransaction(txId, true).map {
      case Some(tx) =>
        tx.successNel[BOValidationFailure]
      case None =>
        BOValidationFailure(TRANS_ID, Invalid, "Unknown tx id").failureNel[TransactionBO]
    }
  }
}
