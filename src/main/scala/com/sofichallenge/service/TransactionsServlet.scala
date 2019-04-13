package com.sofichallenge.service

import org.scalatra._

class TransactionsServlet extends ScalatraServlet {

  get("/") {
    views.html.hello()
  }

}
