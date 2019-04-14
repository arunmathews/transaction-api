package com.sofichallenge.transactionapi.exception

import org.apache.http.HttpStatus._

/**
 *
 */
class RecoverableException(msg: String, cause: Throwable) extends HttpException(SC_SERVICE_UNAVAILABLE, true, msg, cause) {
  def this(cause: Throwable) = this(cause.getMessage, cause)
  def this(msg: String) = this(msg, null)
}
