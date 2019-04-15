package com.sofichallenge.transactionapi.businessobject

case class GetMerchantsSpecifiers(userIds: Seq[Int], limit: Int, sortAsc: Boolean = true)