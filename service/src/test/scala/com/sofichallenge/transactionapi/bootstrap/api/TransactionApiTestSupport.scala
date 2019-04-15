package com.sofichallenge.transactionapi.bootstrap.api

import com.sofichallenge.transactionapi.bootstrap.{TestExecutionContext, TestTablesWithDb}
import com.sofichallenge.transactionapi.dependency.impl.TransactionApiDBImpl
import org.scalatest._

/**
  *
  */
trait TransactionApiTestSupport extends TestTablesWithDb with TestExecutionContext {
  this: Suite =>

  val txApi = new TransactionApiDBImpl(tablesWithDb)(cachedEC)
}
