package com.sofichallenge.transactionapi.service

import com.sofichallenge.transactionapi.service.validation.businessobject.BOValidationFailure
import com.sofichallenge.transactionapi.service.validation._
import org.scalatra.{AsyncResult, FutureSupport}
import scalaz._
import Scalaz._
import com.sofichallenge.transactionapi.handler.TransactionRequestHandler
import com.sofichallenge.transactionapi.service.businessobject.StoreTransOperationOutputBO
import org.scalatra.metrics.MetricsSupport

import scala.concurrent.{ExecutionContext, Future}

/**
  * Servlet that responds to transaction http requests
  *
  * @param reqHandler
  */
class TransactionServlet(reqHandler: TransactionRequestHandler)
  extends TransactionApiStack
    with JsonSupportMixin
    with ErrorSupportMixin
    with FutureSupport
    with MetricsSupport {

  post("/v1/transactions") {
    new AsyncResult() {
      val transCommand = command[ValidateCreateTransactionCommand]
      override val is = {
        TransactionValidationUtils.validateTransaction(transCommand).fold(
          haltOnFailure,
          succ => reqHandler.createTransaction(succ).map(_.fold(
            haltOnFailure,
            succ => succ
          ))
        )
      }
    }
  }

  put("/v1/transactions/:id/void") {
    new AsyncResult() {
      override val is = {
        val maybeTxId = params.get("id")
        TransactionValidationUtils.validateTransId(maybeTxId).fold(
          haltOnFailure,
          succ => reqHandler.voidTransaction(succ).map(_.fold(
            haltOnFailure,
            succ => succ
          ))
        )
      }
    }
  }

  get("/v1/transactions/merchants") {
    new AsyncResult() {
      override val is = {
        val userIds = multiParams.get("user-id")
        val limit = params.get("limit")
        val sortType = params.get("sort-type")
        TransactionValidationUtils.validationGetMerchantsSpecifiers(userIds, limit, sortType).fold(
          haltOnFailure,
          succ => reqHandler.getMerchants(succ).map(_.fold(
            haltOnFailure,
            succ => succ
          ))
        )
      }
    }
  }

  get("/v1/transactions") {
    new AsyncResult() {
      override val is = {
        val userId = params.get("user-id")
        val limit = params.get("limit")
        val offsetDate = params.get("offset-date")
        val offsetInt = params.get("offset-int")
        val offsetBigDec = params.get("offset-price")

        val sortKey = params.get("sort-key")
        val paginationDirection = params.get("pagination-direction")

        TransactionValidationUtils.validateGetTxsSpecifiers(userId, limit, offsetDate, offsetInt, offsetBigDec,
          sortKey, paginationDirection).fold(
          haltOnFailure,
          succ => reqHandler.getTransactions(succ)
        )
      }
    }
  }

  def haltOnFailure(failures: NonEmptyList[BOValidationFailure]): Nothing = {
    haltInvalidRequest(Option(StoreTransOperationOutputBO(validationFailures = Option(failures.toList))))
  }

  override implicit protected def executor: ExecutionContext = ExecutionContext.Implicits.global
}
