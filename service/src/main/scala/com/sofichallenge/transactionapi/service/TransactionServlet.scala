package com.sofichallenge.transactionapi.service

import com.sofichallenge.transactionapi.service.validation.businessobject.BOValidationFailure
import com.sofichallenge.transactionapi.service.validation._
import org.scalatra._
import org.scalatra.{AsyncResult, FutureSupport}
import scalaz._
import Scalaz._
import com.sofichallenge.transactionapi.handler.TransactionRequestHandler
import com.sofichallenge.transactionapi.service.businessobject.StoreTransOperationOutputBO

import scala.concurrent.{ExecutionContext, Future}

class TransactionServlet(reqHandler: TransactionRequestHandler)
  extends TransactionApiStack
    with JsonSupportMixin
    with ErrorSupportMixin
    with FutureSupport {

  post("/v1/transactions") {
    new AsyncResult() {
      val transCommand = command[ValidateCreateTransactionCommand]
      override val is = {
        TransactionValidationUtils.validateTransaction(transCommand).fold(
          haltOnFailure,
          succ => reqHandler.createTransaction(succ)
        )
      }
    }
  }

  put("/v1/transactions/:id/void") {
    new AsyncResult() {
      override val is = {
        val maybeTxId = params.get("id")
        TransactionValidationUtils.validateTransactionId(maybeTxId).fold(
          haltOnFailure,
          succ => reqHandler.voidTransaction(succ).map(_.fold(
            haltOnFailure,
            succ => succ
          ))
        )
      }
    }
  }

  def haltOnFailure(failures: NonEmptyList[BOValidationFailure]): Nothing = {
    haltInvalidRequest(Option(StoreTransOperationOutputBO(validationFailures = Option(failures.toList))))
  }

  override implicit protected def executor: ExecutionContext = ExecutionContext.Implicits.global
}
