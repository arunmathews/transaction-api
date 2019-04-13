package com.sofichallenge.transactionapi.service

import com.sofichallenge.transactionapi.service.validation.businessobject.BOValidationFailure
import com.sofichallenge.transactionapi.service.validation.{TransactionValidationUtils, ValidateCreateTransactionCommand}
import org.scalatra._
import org.scalatra.{AsyncResult, FutureSupport}
import scalaz._
import Scalaz._
import com.sofichallenge.transactionapi.service.businessobject.StoreTransOperationOutputBO

import scala.concurrent.{ExecutionContext, Future}


case class ValidationForm(text: String, email: Option[String], number: Int)

class TransactionListServlet
  extends TransactionApiStack
    with JsonSupportMixin
    with ErrorSupportMixin
    with FutureSupport {

  post("/v1/transactions") {
    new AsyncResult() {
      val transCommand = command[ValidateCreateTransactionCommand]
      override val is = {
        TransactionValidationUtils.createTransaction(transCommand).fold(
          haltOnFailure,
          succ => Future.successful(succ)
        )
      }
    }
  }

  def haltOnFailure(failures: NonEmptyList[BOValidationFailure]): Nothing = {
    haltInvalidRequest(Option(StoreTransOperationOutputBO(validationFailures = Option(failures.toList))))
  }

  override implicit protected def executor: ExecutionContext = ExecutionContext.Implicits.global
}
