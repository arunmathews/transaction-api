package com.sofichallenge.transactionapi.service.validation

import org.json4s.JsonAST.{JDecimal, JString}
import org.json4s._
import org.scalatra.commands.{FieldDescriptor, JsonCommand}
import org.scalatra.util.conversion.TypeConverter

/**
  *
  */
abstract class ValidateBaseCommand[S](implicit mf: Manifest[S]) extends JsonCommand {

  implicit val stringToBigDecimal: TypeConverter[String, BigDecimal] = safe(BigDecimal(_))

  implicit val jsonToBigDec: TypeConverter[JValue, BigDecimal] = safeOption {
    case JDecimal(bigDec) => Some(bigDec)
    case JDouble(double) => Some(BigDecimal(double))
    case JInt(int) => Some(BigDecimal(int))
    case JLong(long) => Some(BigDecimal(long))
    case JString(v) => Some(BigDecimal(v))
    case _ => None
  }
}
