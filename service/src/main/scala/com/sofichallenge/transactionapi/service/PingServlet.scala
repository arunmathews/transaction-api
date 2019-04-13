package com.sofichallenge.transactionapi.service

/**
  *
  */
class PingServlet() extends TransactionApiStack {
  get("/v1/ping") {
    //No op - returns 200. Validates that the service is up
  }
}
