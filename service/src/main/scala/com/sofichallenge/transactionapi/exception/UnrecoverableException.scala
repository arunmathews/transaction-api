package com.sofichallenge.transactionapi.exception

import org.apache.http.HttpStatus._

/**
 *
 */
class UnrecoverableException(msg: String, cause: Throwable) extends HttpException(SC_INTERNAL_SERVER_ERROR, true, msg, cause) {
  def this(cause: Throwable) = this(cause.getMessage, cause)
  def this(msg: String) = this(msg, null)
}
