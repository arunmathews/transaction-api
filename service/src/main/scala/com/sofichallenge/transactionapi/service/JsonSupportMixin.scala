package com.sofichallenge.transactionapi.service

import com.sofichallenge.transactionapi.json.JsonFormats
import org.json4s.Formats
import org.json4s.JsonAST.{JField, JObject, JString, JValue}
import org.scalatra.commands.JacksonJsonParsing
import org.scalatra.json.JacksonJsonSupport

/**
 *
 */
trait JsonSupportMixin
  extends JacksonJsonParsing
  with JacksonJsonSupport {

  before() {
    contentType = formats("json")
  }

  protected implicit lazy val jsonFormats: Formats = JsonFormats.jsonFormat

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys

  protected override def transformResponseBody(body: JValue): JValue = body.underscoreKeys
}