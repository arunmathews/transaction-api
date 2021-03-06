package com.sofichallenge.transactionapi.service.validation.businessobject

/**
  *
  */
object TransactionOperationFieldName {
  case object USER_ID extends BOFieldName("user-id")
  case object MERCHANT_ID extends BOFieldName("merchant-id")
  case object MERCHANT extends BOFieldName("merchant")
  case object PRICE extends BOFieldName("price")
  case object PURCHASE_DATE extends BOFieldName("purchase-date")
  case object TRANS_ID extends BOFieldName("tx-id")
  case object LIMIT extends BOFieldName("limit")
  case object OFFSET_DATE extends BOFieldName("offset_date")
  case object OFFSET_INT extends BOFieldName("offset_int")
  case object OFFSET_BIGDEC extends BOFieldName("offset_big_dec")
  case object USER_IDS extends BOFieldName("user-ids")
}