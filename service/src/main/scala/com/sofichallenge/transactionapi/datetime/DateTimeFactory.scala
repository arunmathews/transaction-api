package com.sofichallenge.transactionapi.datetime

import java.sql.{Timestamp, Date => SDate}
import java.util.{Date => JDate}

import org.joda.time.{DateTime, DateTimeZone, LocalDate}

/**
  * Utility methods to deal with different date/time formats
  */
object DateTimeFactory {
  private val pacificZone = DateTimeZone.forID("US/Pacific")

  def toDateTimeUTC(dateInMillis: Long): DateTime = new DateTime(dateInMillis, DateTimeZone.UTC)

  def toDateTimeUTC(timestamp: Timestamp): DateTime = toDateTimeUTC(timestamp.getTime)

  def toDateTimePacific(timestamp: Timestamp): DateTime = toDateTimeUTC(timestamp.getTime).withZone(pacificZone)

  def toDateTimeUTC(date: JDate): DateTime = new DateTime(date.getTime, DateTimeZone.UTC)

  def dateTimePacific(): DateTime = DateTime.now(pacificZone)
  
  def toLongUTC(dateTime: DateTime): Long = dateTime.toDateTime(DateTimeZone.UTC).getMillis

  def toTimeStamp(dateTime: DateTime): Timestamp = new Timestamp(toLongUTC(dateTime))

  def currentTimeStamp: Timestamp = toTimeStamp(new DateTime(DateTimeZone.UTC))
}
