package com.sofichallenge.transactionapi.bootstrap

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.sofichallenge.transactionapi.bootstrap.TestMagicConstants._
import com.sofichallenge.transactionapi.database.tablemapping.{PostgresTablesWithDb, TablesWithDb}
import org.scalatest._

trait TestTablesWithDb
  extends ConfigLoader
    with BootstrapConfigHelper
    with BeforeAndAfterAll {
  this: Suite =>

  private val dbHostUrl = getFromSysOrConf(dbHostUrlKey, conf)
  private val dbName = getFromSysOrConf(dbNameKey, conf)
  private val jdbcUrl = s"$dbHostUrl/$dbName"
  val cpds = new ComboPooledDataSource
  cpds.setJdbcUrl(jdbcUrl)
  cpds.setUser(getFromSysOrConf(dbUserKey, conf))
  cpds.setMinPoolSize(5)
  cpds.setMaxPoolSize(12)
  cpds.setCheckoutTimeout(10000)
  cpds.setNumHelperThreads(6)

  val tablesWithDb = new PostgresTablesWithDb(cpds)

  override def afterAll(): Unit = {
    try closeDbConnection(tablesWithDb, cpds)
    finally super.afterAll()
  }

  private def closeDbConnection(tablesWithDb: TablesWithDb, cpds: ComboPooledDataSource) {
    tablesWithDb.db.close()
    cpds.close()
  }
}
