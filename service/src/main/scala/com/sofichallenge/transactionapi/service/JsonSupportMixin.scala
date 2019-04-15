package com.sofichallenge.transactionapi.service

import com.sofichallenge.transactionapi.json.JsonFormats
import org.json4s
import org.json4s.{Formats, JField, JValue}
import org.json4s.JsonAST.{JField, JObject, JString, JValue}
import org.scalatra.commands.JacksonJsonParsing
import org.scalatra.json.JacksonJsonSupport

/**
 * Mixin json specific logic to the servlet
 */
trait JsonSupportMixin
  extends JacksonJsonParsing
  with JacksonJsonSupport {

  before() {
    contentType = formats("json")
  }

  protected implicit lazy val jsonFormats: Formats = JsonFormats.jsonFormat
}