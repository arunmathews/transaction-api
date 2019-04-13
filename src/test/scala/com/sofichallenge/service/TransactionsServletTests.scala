package com.sofichallenge.service

import org.scalatra.test.scalatest._

class TransactionsServletTests extends ScalatraFunSuite {

  addServlet(classOf[TransactionsServlet], "/*")

  test("GET / on TransactionsServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
