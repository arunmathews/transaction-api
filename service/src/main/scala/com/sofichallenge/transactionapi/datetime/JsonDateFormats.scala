package com.sofichallenge.transactionapi.datetime

import org.joda.time.{DateTime, LocalDate}
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormat}
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}
import org.scalatra.commands.JodaDateFormats

/**
  *
  */

object JsonDateFormats {
  val localDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

  val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")

}

object DateTimeSerializer extends CustomSerializer[DateTime] (format =>
  (
    {
      case JString(s) =>
        JsonDateFormats.dateTimeFormatter.parseDateTime(s)
      case JNull => null
    },
    {
      case x: DateTime =>
        JString(JsonDateFormats.dateTimeFormatter.print(x))
    }
    )
)

object LocalDateSerializer extends CustomSerializer[LocalDate] (format =>
  (
    {
      case JString(s) =>
        JsonDateFormats.localDateFormatter.parseLocalDate(s)
      case JNull => null
    },
    {
      case x: LocalDate =>
        JString(JsonDateFormats.localDateFormatter.print(x))
    }
    )
)

