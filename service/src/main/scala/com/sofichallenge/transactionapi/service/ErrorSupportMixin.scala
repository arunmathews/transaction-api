package com.sofichallenge.transactionapi.service

import com.sofichallenge.transactionapi.exception.{BaseException, HttpException}
import org.scalatra._
import org.slf4j.LoggerFactory

/**
 *
 */
trait ErrorSupportMixin {
  self: ScalatraBase =>

  private val logger = LoggerFactory.getLogger(getClass)

  error {
    case e: HttpException =>
      if (e.shouldLog) {
        logger.error("Got an error while processing the request: ", e)
      }
      ActionResult(e.responseCode, createBody(e), Map.empty)
    case e: BaseException =>
      logger.error("Got an error while processing the request: ", e)
      ServiceUnavailable(createBody(e))
    case e: RuntimeException =>
      logger.error("Got an error while processing the request: ", e)
      ServiceUnavailable(createBody(e.getMessage))
    case e: Throwable =>
      logger.error("Got an error while processing the request: ", e)
      ServiceUnavailable(createBody(e.getMessage))
  }

  def haltRequest(status: Int, message: String): Nothing = {
    halt(status, createBody(message))
  }

  def haltRequest(status: Int, maybeMessage: Option[String]): Nothing = {
    maybeMessage.fold(halt(status, ""))(message => haltRequest(status, message))
  }

  def haltInvalidRequest[T: Manifest](maybeT: Option[T]): Nothing = {
    maybeT.fold(halt(400, ""))(t => halt(400, t))
  }

  private def createBody(ex: BaseException): String = {
    if (isDevelopmentMode) {
      ex.getMessage
    }
    else {
      ""
    }
  }

  private def createBody(message: String): String = {
    if (isDevelopmentMode) {
      message
    }
    else {
      ""
    }
  }
}
