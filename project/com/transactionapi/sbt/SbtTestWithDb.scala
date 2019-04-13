package com.transactionapi.sbt

import com.transactionapi.sbt.SbtDatabaseTasks
import com.transactionapi.sbt.SbtDbParams
import com.transactionapi.sbt.SbtFlywayMigrateDb._
import sbt.Keys._
import sbt._

import scala.language.postfixOps

object SbtTestWithDb extends AutoPlugin {
  def testWithDbTask(dbHostUrl: String, user: String, password: Option[String], defaultDb: String, testDb: String,
                     flywayLocation: String, flywayTable: Option[String]) =  Def.inputTaskDyn {

    testWithDbTaskImpl(dbHostUrl, user, password, defaultDb, testDb, flywayLocation, flywayTable)
  }

  def testWithDbTaskImpl(dbHostUrl: String, user: String, password: Option[String], defaultDb: String,
                         testDb: String, flywayLocation: String, flywayTable: Option[String]) = {
    val testDbUrl = s"$dbHostUrl/$testDb"
    val flywayParams = SbtDbParams(testDbUrl, user, password, flywayTable)
    Def.sequential(Seq(SbtDatabaseTasks.createNewDbTask(dbHostUrl, user, password, defaultDb, testDb),
      migrateFlywayDbImpl(flywayParams, flywayLocation)),
      (test in Test).toTask).andFinally(SbtDatabaseTasks.dropDbImpl(dbHostUrl, user, password, defaultDb, testDb))
  }
}
