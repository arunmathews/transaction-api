package com.sofichallenge.transactionapi.service.validation

import com.sofichallenge.transactionapi.service.validation.businessobject.{BOFieldName, BOValidationFailure}
import scalaz._
import Scalaz._
import com.sofichallenge.transactionapi.businessobject.TransactionBO
import com.sofichallenge.transactionapi.datetime.JsonDateFormats
import com.sofichallenge.transactionapi.service.validation.businessobject.BOValidationFailureCode._
import com.sofichallenge.transactionapi.service.validation.businessobject.TransactionOperationFieldName._
import org.joda.time.DateTime
/**
  *
  */
object TransactionValidationUtils {

  def validateTransaction(createCommand: ValidateCreateTransactionCommand):
  ValidationNel[BOValidationFailure, TransactionBO] = {
    (checkMissingNonEmpty(TRANS_ID, createCommand.txId.value).toValidationNel |@|
      checkMissingNonEmpty(USER_ID, createCommand.userId.value).toValidationNel |@|
      checkMissingNonEmpty(MERCHANT_ID, createCommand.merchantId.value).toValidationNel |@|
      checkMissingNonEmpty(MERCHANT, createCommand.merchant.value).toValidationNel |@|
      validatePrice(createCommand.price.value).toValidationNel |@|
      validatePurchaseDate(createCommand.purchaseDate.value).toValidationNel |@|
      false.successNel[BOValidationFailure])(TransactionBO.apply)
  }

  def validateTransactionId(maybeId: Option[String]): ValidationNel[BOValidationFailure, String] = {
    checkMissingNonEmpty(TRANS_ID, maybeId).toValidationNel
  }

  private def checkMissingNonEmpty[T](fieldName: BOFieldName, maybeT: Option[T]): Validation[BOValidationFailure, T] = {
    maybeT.fold(BOValidationFailure(fieldName, Missing, s"$fieldName is missing").failure[T])(
      t => t.success[BOValidationFailure])
  }

  private def validatePrice(maybeCurrencyString: Option[String]): Validation[BOValidationFailure, BigDecimal] = {
    checkAndReturnCurrencyBigDecimal(maybeCurrencyString, PRICE)
  }

  private def checkAndReturnCurrencyBigDecimal(maybeCurrencyString: Option[String], fieldName: BOFieldName):
  Validation[BOValidationFailure, BigDecimal] = {
    checkAndReturnType(maybeCurrencyString, fieldName, validateCurrencyBigDecimal)
  }

  private def validateCurrencyBigDecimal(fieldName: BOFieldName, currencyString: String):
  Validation[BOValidationFailure, BigDecimal] = {
    Validation.fromTryCatchThrowable[BigDecimal, Throwable](BigDecimal(currencyString)).leftMap(_ =>
      BOValidationFailure(fieldName, Invalid, s"Invalid field $fieldName: $currencyString"))
  }

  private def validatePurchaseDate(maybeDtString: Option[String]): Validation[BOValidationFailure, DateTime] = {
    checkAndReturnDateTime(maybeDtString, PURCHASE_DATE)
  }

  private def checkAndReturnDateTime(maybeDtString: Option[String], fieldName: BOFieldName):
  Validation[BOValidationFailure, DateTime] = {
    checkAndReturnType(maybeDtString, fieldName, validateDateTime)
  }

  private def validateDateTime(fieldName: BOFieldName, dateString: String):
  Validation[BOValidationFailure, DateTime] = {
    Validation.fromTryCatchThrowable[DateTime, Throwable](
      DateTime.parse(dateString, JsonDateFormats.dateTimeFormatter)).leftMap(_ =>
      BOValidationFailure(fieldName, Invalid, s"Invalid format for $dateString"))
  }

  private def checkAndReturnType[T](maybeString: Option[String], fieldName: BOFieldName,
                                    f: (BOFieldName, String) => Validation[BOValidationFailure, T]):
  Validation[BOValidationFailure, T] = {
    maybeString.fold( BOValidationFailure(fieldName, Missing, s"$fieldName is Missing").failure[T])(
      typeString => f(fieldName, typeString))
  }
}
