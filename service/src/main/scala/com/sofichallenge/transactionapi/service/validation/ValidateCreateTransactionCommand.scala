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

  val userId: Field[Int] = asInt("user-id").required
  val merchantId: Field[Int] = asInt("merchant-id").required
  val merchant: Field[String] = asString("merchant").required.notBlank
  val price: Field[String] = asString("price").required.notBlank
  val purchaseDate: Field[String] = asString("purchase-date").required.notBlank
  val txId: Field[Int] = asInt("tx-id").required
}