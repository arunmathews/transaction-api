package com.sofichallenge.transactionapi.bootstrap

import java.util.concurrent.Executors

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.scalatest.Suite

import scala.concurrent.ExecutionContext

/**
  *
  */
trait TestExecutionContext {
  this: Suite =>

  val threadBuilder = new ThreadFactoryBuilder()
  threadBuilder.setNameFormat(s"test-execution-context-${getClass.getSimpleName}")

  val cachedEC = ExecutionContext.fromExecutor(Executors.newCachedThreadPool(threadBuilder.build()))
}