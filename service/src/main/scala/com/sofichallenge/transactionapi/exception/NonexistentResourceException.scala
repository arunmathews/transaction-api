package com.sofichallenge.transactionapi.exception

import org.apache.http.HttpStatus._

class NonexistentResourceException(msg: String, cause: Throwable)
  extends HttpException(SC_NOT_FOUND, true, msg, cause) {
   def this(cause: Throwable) = this(cause.getMessage, cause)
   def this(msg: String) = this(msg, null)
}
