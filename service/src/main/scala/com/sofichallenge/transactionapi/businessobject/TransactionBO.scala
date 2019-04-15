package com.sofichallenge.transactionapi.businessobject

import com.sofichallenge.transactionapi.datetime.JsonDateFormats
import com.sofichallenge.transactionapi.exception.UnrecoverableException
import org.joda.time.DateTime
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JObject, JString}
import org.json4s.JsonDSL._

case class TransactionBO(txId: Int, userId: Int, merchantId: Int, merchant: String,
                         price: BigDecimal, purchaseDate: DateTime, void: Boolean)

object TransactionBO {

  /**
    * Custom serializer for serializing transactions in the response
    */
  object TransactionBOSerializer extends CustomSerializer[TransactionBO] (format =>
    (
      {
        case JObject(_) =>
          throw new UnrecoverableException(s"No unmarshalling of transaction json supported this way")
        case JNull => null
      },
      {
        case tx: TransactionBO =>
          ("tx-id" -> tx.txId) ~ ("user-id" -> tx.userId) ~ ("merchant-id" -> tx.merchantId) ~
            ("merchant" -> tx.merchant) ~ ("price" -> tx.price) ~
            ("purchase-date" -> JsonDateFormats.dateTimeFormatter.print(tx.purchaseDate)) ~ ("void" -> tx.void)
      }
    )
  )
}