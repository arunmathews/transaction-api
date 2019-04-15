package com.sofichallenge.transactionapi.database.tablemapping

import com.mchange.v2.c3p0.ComboPooledDataSource

/**
  * Postgres specific slick profile
  */
class PostgresTablesWithDb(cpds: ComboPooledDataSource) extends TablesWithDb {
  override val profile = slick.jdbc.PostgresProfile

  import profile.api._

  override val db = Database.forDataSource(cpds,None)
}
