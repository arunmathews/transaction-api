package com.sofichallenge.transactionapi.service.validation

import com.sofichallenge.transactionapi.service.validation.businessobject.{BOFieldName, BOValidationFailure}
import scalaz._
import Scalaz._
import com.sofichallenge.transactionapi.businessobject._
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

  def validateTransId(maybeId: Option[String]): ValidationNel[BOValidationFailure, Int] = {
    validateId(TRANS_ID, maybeId)
  }

  def validationGetMerchantsSpecifiers(maybeUserIds: Option[Seq[String]],
                                       maybeLimit: Option[String],
                                       maybeSortDirection: Option[String]):
  ValidationNel[BOValidationFailure, GetMerchantsSpecifiers] = {
    val isAsc = !maybeSortDirection.contains("top")
    val defaultLimit = 3

    (validateSeq(USER_IDS, maybeUserIds, validateIntVal) |@|
      ifPresentValidate(LIMIT, maybeLimit, validateIntVal).toValidationNel.map(
        (maybeL: Option[Int]) => maybeL.getOrElse(defaultLimit)) |@|
      isAsc.successNel[BOValidationFailure])(GetMerchantsSpecifiers.apply)
  }

  def validateGetTxsSpecifiers(maybeUserId: Option[String],
                                maybeLimit: Option[String],
                                maybeOffsetDate: Option[String],
                                maybeOffsetInt: Option[String],
                                maybeOffsetBigDec: Option[String],
                                maybeSortKey: Option[String],
                                maybePaginationDirection: Option[String]):
  ValidationNel[BOValidationFailure, GetTransactionsSpecifiers] = {
    val isForwardPagination = !maybePaginationDirection.contains("previous")
    val defaultLimit = 5
    (validateId(USER_ID, maybeUserId) |@|
      ifPresentValidate(LIMIT, maybeLimit, validateIntVal).toValidationNel.map(
        (maybeL: Option[Int]) => maybeL.getOrElse(defaultLimit)) |@|
      ifPresentValidate(OFFSET_DATE, maybeOffsetDate, validateDateTime).toValidationNel |@|
      ifPresentValidate(OFFSET_INT, maybeOffsetInt, validateIntVal).toValidationNel |@|
      ifPresentValidate(OFFSET_BIGDEC, maybeOffsetBigDec, validateCurrencyBigDecimal).toValidationNel |@|
      maybeSortKey.successNel[BOValidationFailure] |@|
      isForwardPagination.successNel[BOValidationFailure])(GetTransactionsSpecifiers.apply)
  }

  private def validateSeq[T](seqFieldName: BOFieldName,
                             maybeSeq: Option[Seq[String]],
                             validateT: (BOFieldName, String) => Validation[BOValidationFailure, T]):
  ValidationNel[BOValidationFailure, Seq[T]] = {
    maybeSeq.fold(BOValidationFailure(seqFieldName, Missing, s"Missing $seqFieldName").failureNel[Seq[T]])(strings => {
      val tVals = strings.map(s => validateT(seqFieldName, s).map(List(_)).toValidationNel)
      tVals.reduce(_ +++ _) match {
        case Failure(e) =>
          BOValidationFailure(seqFieldName, Invalid, s"$seqFieldName Invalid").failureNel
        case Success(xs) => xs.success[BOValidationFailure].toValidationNel
      }
    })
  }

  private def ifPresentValidate[T](fieldName: BOFieldName,
                                   maybeString: Option[String],
                                   f: (BOFieldName, String) => Validation[BOValidationFailure, T]):
  Validation[BOValidationFailure, Option[T]] = {
    maybeString.fold(Option.empty[T].success[BOValidationFailure])(string => f(fieldName, string).rightMap(Option.apply))
  }

  private def validateId(fieldName: BOFieldName, maybeId: Option[String]): ValidationNel[BOValidationFailure, Int] = {
    checkMissingNonEmpty(fieldName, maybeId).disjunction.flatMap(s => validateInt(fieldName, s)).validationNel
  }

  private def validateIntVal(fieldName: BOFieldName, intString: String): Validation[BOValidationFailure, Int] = {
    validateInt(fieldName: BOFieldName, intString: String).validation
  }

  private def validateInt(fieldName: BOFieldName, intString: String): BOValidationFailure \/ Int = {
    \/.fromTryCatchThrowable[Int, Throwable](intString.toInt).
      leftMap(_ => BOValidationFailure(TRANS_ID, Invalid,
        s"Invalid field $TRANS_ID: $intString"))
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
