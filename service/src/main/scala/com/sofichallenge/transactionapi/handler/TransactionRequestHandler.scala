package com.sofichallenge.transactionapi.handler

import com.sofichallenge.transactionapi.businessobject._
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
  def createTransaction(txBO: TransactionBO): Future[ValidationNel[BOValidationFailure, TransactionBO]] = {
    transactionApi.getTransaction(txBO.txId).flatMap {
      case Some(tx) =>
        Future.successful(
          BOValidationFailure(
            TRANS_ID, Duplicate, s"transaction with id - ${tx.txId} already present").failureNel[TransactionBO])
      case None =>
        transactionApi.createTransaction(txBO).map(_.successNel[BOValidationFailure])
    }
  }

  def voidTransaction(txId: Int): Future[ValidationNel[BOValidationFailure, TransactionBO]] = {
    transactionApi.updateTransaction(txId, void = true).map {
      case Some(tx) =>
        tx.successNel[BOValidationFailure]
      case None =>
        BOValidationFailure(TRANS_ID, Invalid, s"Unknown tx id - $txId").failureNel[TransactionBO]
    }
  }

  def getMerchants(specs: GetMerchantsSpecifiers):
  Future[ValidationNel[BOValidationFailure, SearchMerchantsResultsBO]] = {
    transactionApi.getTransactionCount(specs.userIds).flatMap({
      case countMap if countMap.isEmpty =>
        Future.successful(BOValidationFailure(USER_IDS, Invalid,
          s"No transactions for provided user ids - ${specs.userIds}").failureNel[SearchMerchantsResultsBO])
      case countMap =>
        val invalidUsers = countMap.filter {
          case (id, count) => count < TransactionMagicConstants.minTxsCount
        }
        if (invalidUsers.nonEmpty) {
          Future.successful(BOValidationFailure(USER_IDS, Invalid,
            s"Not enough transactions for user ids - ${invalidUsers.keySet}").failureNel[SearchMerchantsResultsBO])
        }
        else {
          transactionApi.getMerchants(specs).map(merchants =>
            SearchMerchantsResultsBO(merchants).successNel[BOValidationFailure])
        }
    })
  }

  def getTransactions(specs: GetTransactionsSpecifiers): Future[SearchTxsResultsBO] = {
    transactionApi.getTransactions(specs)
  }
}
