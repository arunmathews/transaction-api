package com.sofichallenge.transactionapi.datetime

import org.joda.time.{DateTime, LocalDate}
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormat}
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}
import org.scalatra.commands.JodaDateFormats

/**
  * Custom json date format for joda time
  */
object JsonDateFormats {
  val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC()

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

