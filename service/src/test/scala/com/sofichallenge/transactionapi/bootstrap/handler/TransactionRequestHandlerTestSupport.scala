package com.sofichallenge.transactionapi.bootstrap.handler

import com.sofichallenge.transactionapi.bootstrap.api.TransactionApiTestSupport
import com.sofichallenge.transactionapi.handler.TransactionRequestHandler
import org.scalatest._

/**
  *
  */
trait TransactionRequestHandlerTestSupport extends TransactionApiTestSupport {
  this: Suite =>
  val txHandler = new TransactionRequestHandler(txApi)(cachedEC)
}
