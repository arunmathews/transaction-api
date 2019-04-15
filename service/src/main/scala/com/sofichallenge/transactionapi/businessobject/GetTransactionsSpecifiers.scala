package com.sofichallenge.transactionapi.businessobject

import org.joda.time.DateTime

case class GetTransactionsSpecifiers(userId: Int,
                                     limit: Int,
                                     offsetDate: Option[DateTime] = None,
                                     offsetInt: Option[Int] = None,
                                     offsetBigDec: Option[BigDecimal] = None,
                                     sortKey: Option[String] = None,
                                     forwardCutOffDirection: Boolean = true)