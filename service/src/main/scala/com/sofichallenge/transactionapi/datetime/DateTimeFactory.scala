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

  def toDate(dateTime: DateTime): JDate = dateTime.toDate

  def currentTimeStamp: Timestamp = toTimeStamp(new DateTime(DateTimeZone.UTC))

  def toSqlDate(dateTime: DateTime): SDate = new SDate(dateTime.getMillis)

  def toSqlDate(localDate: LocalDate): SDate = new SDate(localDate.toDate.getTime)

  def toLocalDate(date: SDate) = LocalDate.fromDateFields(date)

  def currentSqlDate: SDate = toSqlDate(new DateTime(DateTimeZone.UTC))

  def localEasternToTimestamp(localDate: LocalDate): Timestamp = toTimeStamp(localEasternToUTCDateTime(localDate))

  def localEasternToUTCDateTime(localDate: LocalDate): DateTime = {
    localToUTCDateTimeWithTimeZone(localDate, DateTimeZone.forID("US/Eastern"))
  }

  def localPacificToTimestamp(localDate: LocalDate): Timestamp = toTimeStamp(localPacificToUTCDateTime(localDate))

  def localDatePacific(): LocalDate = LocalDate.now(pacificZone)

  def pacificDateTimeStartOfDay(): DateTime = localPacificToDateTime(localDatePacific())
  
  def localPacificToUTCDateTime(localDate: LocalDate): DateTime = {
    localToUTCDateTimeWithTimeZone(localDate, pacificZone)
  }

  def localPacificToDateTime(localDate: LocalDate): DateTime = {
    localToDateTime(localDate, pacificZone)
  }

  def UTCDateTimeToLocalEastern(dateTime: DateTime): LocalDate = {
    dateTimeToLocal(dateTime, DateTimeZone.forID("US/Eastern"))
  }

  def UTCDateTimeToLocalPacific(dateTime: DateTime): LocalDate = {
    dateTimeToLocal(dateTime, pacificZone)
  }

  def toLocalDatePacific(dateTime: DateTime): LocalDate = {
    dateTimeToLocal(dateTime, pacificZone)
  }

  private def localToUTCDateTimeWithTimeZone(localDate: LocalDate, dateTimeZone: DateTimeZone): DateTime = {
    val dateTimeWithZone = localToDateTime(localDate, dateTimeZone)
    dateTimeWithZone.toDateTime(DateTimeZone.UTC)
  }

  private def localToDateTime(localDate: LocalDate, dateTimeZone: DateTimeZone): DateTime = {
    localDate.toDateTimeAtStartOfDay(dateTimeZone)
  }

  private def dateTimeToLocal(dateTime: DateTime, dateTimeZone: DateTimeZone): LocalDate = {
    val timeZoneDate = dateTime.toDateTime(dateTimeZone)
    timeZoneDate.toLocalDate
  }
}
