package com.sofichallenge.transactionapi.service.validation

import com.sofichallenge.transactionapi.json.JsonFormats
import org.joda.time.DateTime
import org.json4s.Formats
import org.scalatra.commands._

/**
 * Extract create transaction data
 */
class ValidateCreateTransactionCommand extends ValidateBaseCommand {
  override protected implicit def jsonFormats: Formats = JsonFormats.jsonFormat

  val userId: Field[String] = asString("user-id").required.notBlank
  val merchantId: Field[String] = asString("merchant-id").required.notBlank
  val merchant: Field[String] = asString("merchant").required.notBlank
  val price: Field[String] = asString("price").required.notBlank
  val purchaseDate: Field[String] = asString("purchase-date").required.notBlank
  val txId: Field[String] = asString("tx-id").required.notBlank
}