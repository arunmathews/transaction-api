package com.sofichallenge.transactionapi.json

import com.sofichallenge.transactionapi.businessobject.TransactionBO.TransactionBOSerializer
import com.sofichallenge.transactionapi.datetime.DateTimeSerializer
import com.sofichallenge.transactionapi.service.validation.businessobject._
import enumeratum.Json4s
import org.json4s.{DefaultFormats, Formats}

/**
  *
  */
object JsonFormats {
  val jsonFormat: Formats = DefaultFormats.withBigDecimal +
    DateTimeSerializer +
    TransactionBOSerializer +
    BOFieldNameSerializer +
    Json4s.serializer(BOValidationFailureCode)
}