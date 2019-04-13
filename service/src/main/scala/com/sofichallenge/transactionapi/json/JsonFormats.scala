package com.sofichallenge.transactionapi.json

import com.sofichallenge.transactionapi.datetime.{DateTimeSerializer, LocalDateSerializer}
import com.sofichallenge.transactionapi.service.validation.businessobject.{BOFieldNameSerializer, BOValidationFailureCode}
import enumeratum.Json4s
import org.json4s.{DefaultFormats, Formats}

/**
  *
  */
object JsonFormats {
  val jsonFormat: Formats = DefaultFormats.withBigDecimal +
    LocalDateSerializer +
    DateTimeSerializer +
    BOFieldNameSerializer +
    Json4s.serializer(BOValidationFailureCode)
}