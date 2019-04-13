package com.sofichallenge.transactionapi.exception

/**
 * Abstract base class of exceptions
 */
abstract class BaseException(msg: String, cause: Throwable) extends RuntimeException(msg, cause) {
  def this(msg: String) = this(msg, null)
  def this(cause: Throwable) = this(cause.getMessage, cause)
}
