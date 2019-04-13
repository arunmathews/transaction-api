package com.transactionapi.sbt

import io.github.davidmweber.FlywayPlugin
import sbt._

/**
  *
  */
object SbtFlywayMigrateDb {

  lazy val migrateFlywayDbTables = inputKey[Unit]("Flyway migrate db using credentials from custom file")

  def migrateFlywayDbTask(defaultPropertiesPath: String, dbFlywayLocation: String) =  Def.inputTaskDyn {
    val args = Def.spaceDelimited().parsed
    migrateFlywayDbCode(args, defaultPropertiesPath, dbFlywayLocation)
  }

  def migrateFlywayDbCode(args: Seq[String], defaultPropertiesPath: String, dbFlywayLocation: String) = {
    val flywayParams = ExtractParamsUtils.extractDbParamsFromFile(args, defaultPropertiesPath)
    migrateFlywayDbImpl(flywayParams, dbFlywayLocation)
  }

  def migrateFlywayDbImpl(flywayParams: SbtDbParams, dbFlywayLocation: String) = Def.taskDyn {
    val urlKey = "flyway.url"
    val userKey = "flyway.user"
    val passwordKey = "flyway.password"
    val tableKey = "flyway.table"
    val locationKey = "flyway.locations"
    val baselineKey = "flyway.baselineOnMigrate"

    System.setProperty(urlKey, flywayParams.dbUrl)
    System.setProperty(userKey, flywayParams.dbUser)
    flywayParams.dbPassword.foreach(System.setProperty(passwordKey, _))
    flywayParams.dbFlywayTable.foreach(System.setProperty(tableKey, _))
    System.setProperty(locationKey, dbFlywayLocation)
    System.setProperty(baselineKey, "true")

    FlywayPlugin.autoImport.flywayMigrate.andFinally {
      System.clearProperty(urlKey)
      System.clearProperty(userKey)
      System.clearProperty(passwordKey)
      System.clearProperty(tableKey)
      System.clearProperty(locationKey)
      System.clearProperty(baselineKey)
    }
  }
}
