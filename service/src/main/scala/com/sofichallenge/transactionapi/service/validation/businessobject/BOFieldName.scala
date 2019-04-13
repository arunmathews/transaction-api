package com.sofichallenge.transactionapi.service.validation.businessobject

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

/**
 *
 */
abstract class BOFieldName(val fieldName: String) {
  override def toString = fieldName
}

object BOFieldNameSerializer extends CustomSerializer[BOFieldName] (format =>
  (
    {
      //Only marshalling no unmarshalling
      case JString(s) => null
      case JNull => null
    },
    {
      case x: BOFieldName => JString(x.toString)
    }
  )
)