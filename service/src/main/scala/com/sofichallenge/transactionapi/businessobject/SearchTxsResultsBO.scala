package com.sofichallenge.transactionapi.businessobject

/**
  *
  */
case class SearchTxsResultsBO(transactions: Seq[TransactionBO], prevOffset: Option[String], nextOffset: Option[String],
                                 count: Int)
