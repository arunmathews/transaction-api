package com.sofichallenge.transactionapi.service.validation.businessobject

import enumeratum.EnumEntry
import enumeratum.Enum
import enumeratum.EnumEntry.Snakecase
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

sealed trait BOValidationFailureCode extends EnumEntry with Snakecase

object BOValidationFailureCode extends Enum[BOValidationFailureCode]{
  val values = findValues

  case object Invalid extends BOValidationFailureCode

  case object Missing extends BOValidationFailureCode

  case object Unknown extends BOValidationFailureCode
}