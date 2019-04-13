package com.sofichallenge.transactionapi.businessobject

import org.joda.time.DateTime

/**
  *
  */
case class TransactionBO(transactionId: String, userId: String, merchantId: String, merchant: String,
                         price: BigDecimal, purchasedDate: DateTime)