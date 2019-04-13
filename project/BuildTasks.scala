import java.io.File

import com.earldouglas.xwp.JettyPlugin._
import SbtDatabaseTasks._
import com.earldouglas.xwp.JettyPlugin
import com.earldouglas.xwp.JettyPlugin.autoImport._
import com.earldouglas.xwp.ContainerPlugin.autoImport._
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import sbt.Keys._
import sbt._

object BuildTasks {
  lazy val setupCommonDb = taskKey[Unit]("Create new db and use flyway to migrate sql files from migration package")
  lazy val setupTestDb = taskKey[Unit]("Create test postgres db for running tests")
  lazy val teardownTestDb = inputKey[Unit]("Drop test db if it exists")
  lazy val testWithDb = inputKey[Unit]("Create test postgres, run tests, drop test db")

  val defaultPropertiesPath = "override/local.properties"
  val testPropertiesPath = "override/test.properties"
  val slickPostgresDriverFQCN = "slick.jdbc.PostgresProfile"
  val slickCodeGeneratorFQCN = "slick.codegen.SourceCodeGenerator"
  val dbRootPkg = "com.sofichallenge.transactionapi.database"
  val dbGenSourcePkg = s"$dbRootPkg.tablemapping"
  val dbFlywayPkg = s"$dbRootPkg.migration"
  val dbGenSourceDir = dbGenSourcePkg.replaceAll("\\.","/")

  val baseConf = ConfigFactory.load()
  val defaultFileConf = ConfigFactory.parseFile(new File(defaultPropertiesPath))
  val defaultConf = ConfigFactory.load(defaultFileConf).withFallback(baseConf)
  val testFileConf = ConfigFactory.parseFile(new File(testPropertiesPath))
  val testConf = ConfigFactory.load(testFileConf).withFallback(baseConf)
  val dbHostUrl = ExtractParamsUtils.getFromSysOrConf("db_host_url", defaultConf)
  val defaultDb = ExtractParamsUtils.getFromSysOrConf("default_db", defaultConf)
  val dbDefaultUser = ExtractParamsUtils.getFromSysOrConf("db_default_user", defaultConf)
  val dbDefaultPassword = ExtractParamsUtils.getFromSysOrConfOpt("db_default_password", defaultConf)
  val commonDb = ExtractParamsUtils.getFromSysOrConf("common_db", defaultConf)
  val testDb = ExtractParamsUtils.getFromSysOrConf("test_db", testConf)
  val flywayTable = defaultConf.as[Option[String]]("db_flyway_table")
  val dbFlywayLocation = s"classpath:$dbRootPkg.migration"
  val dbFlywayTestLocation = s"filesystem:flyway/src/main/resources/com/sofichallenge/transactionapi/database/migration,filesystem:flyway/src/test/resources/com/sofichallenge/transactionapi/database/migration"

  def createCommonDbAndTablesTask() = Def.taskDyn(createDbAndCreateTablesTask(dbHostUrl, dbDefaultUser,
    dbDefaultPassword, defaultDb, commonDb, dbFlywayLocation, flywayTable))

  def createTestDbAndTablesTask() = Def.taskDyn(createDbAndCreateTablesTask(dbHostUrl, dbDefaultUser,
    dbDefaultPassword, defaultDb, testDb, dbFlywayTestLocation, flywayTable))
}
