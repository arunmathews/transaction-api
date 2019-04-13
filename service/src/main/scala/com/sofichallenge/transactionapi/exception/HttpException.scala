package com.sofichallenge.transactionapi.exception

/**
 * Base class of exceptions that have an associated http status code. These are sent to callers
 */
class HttpException(val responseCode: Int, val shouldLog: Boolean, msg: String, cause: Throwable) extends BaseException(msg, cause) {
  def this(responseCode: Int, cause: Throwable) = this(responseCode, true, cause.getMessage, cause)
  def this(responseCode: Int, msg: String) = this(responseCode, true, msg, null)
  def this(responseCode: Int, shouldLog: Boolean, cause: Throwable) = this(responseCode, shouldLog, cause.getMessage, cause)
  def this(responseCode: Int, shouldLog: Boolean, msg: String) = this(responseCode, shouldLog, msg, null)
}