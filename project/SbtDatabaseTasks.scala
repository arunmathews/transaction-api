import org.postgresql.util.PSQLException
import sbt.{AutoPlugin, Def, Task}
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import SbtFlywayMigrateDb.migrateFlywayDbImpl
import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Definition of database related tasks to be run from sbt - create db, drop db etc.
  */
object SbtDatabaseTasks extends AutoPlugin {
  val jdbcDriver = "org.postgresql.Driver"
  val actionTimeout = 10 seconds

  def createDbInputTask(dbHostUrl: String, user: String, password: Option[String], defaultDb: String,
                        newDb: String, flywayLocation: String, flywayTable: Option[String]) = Def.inputTaskDyn {
    createDbAndCreateTablesTask(dbHostUrl, user, password, defaultDb, newDb, flywayLocation, flywayTable)
  }

  def createDbAndCreateTablesTask(dbHostUrl: String, user: String, password: Option[String], defaultDb: String,
                                  newDb: String, flywayLocation: String, flywayTable: Option[String]):
  Def.Initialize[Task[Unit]] = {
    val newDbUrl = s"$dbHostUrl/$newDb"
    println(s"Creating database and migrating schema at location: $newDbUrl")
    val flywayParams = SbtDbParams(newDbUrl, user, password, flywayTable)
    Def.sequential(Seq(createNewDbTask(dbHostUrl, user, password, defaultDb, newDb)),
      migrateFlywayDbImpl(flywayParams, flywayLocation))
  }

  def dropDbInputTask(dbHostUrl: String, user: String, password: Option[String], defaultDb: String,
                      dropDb: String) = Def.inputTaskDyn {
    println(s"Dropping database at location: $dbHostUrl/$dropDb")
    dropDbTask(dbHostUrl, user, password, defaultDb, dropDb)
  }

  def createNewDbTask(dbHostUrl: String, user: String, password: Option[String], defaultDb: String,
                      newDb: String): Def.Initialize[Task[Unit]] =
    Def.task(createNewDbImpl(dbHostUrl, user, password, defaultDb, newDb))

  private def createNewDbImpl(dbHostUrl: String, user: String, maybePassword: Option[String], defaultDb: String,
                              newDb: String): Unit = {
    val defaultDbUrl = s"$dbHostUrl/$defaultDb"
    val db = createDbConnection(user, maybePassword, defaultDbUrl)
    try {
      Await.result(db.run(sqlu"CREATE DATABASE #$newDb"), actionTimeout)
    }
    finally db.close()
  }

  private def dropDbTask(dbHostUrl: String, user: String, password: Option[String], defaultDb: String,
                         drodBb: String): Def.Initialize[Task[Unit]] =
    Def.task(dropDbImpl(dbHostUrl, user, password, defaultDb, drodBb))

  def dropDbImpl(dbHostUrl: String, user: String, password: Option[String], defaultDb: String,
                 dropDb: String): Unit = {
    {
      val defaultDbUrl = s"$dbHostUrl/$defaultDb"
      val db =createDbConnection(user, password, defaultDbUrl)
      try {
        Await.result(db.run(sqlu"DROP DATABASE #$dropDb"), actionTimeout)
      }
      catch {
        // ignore failure if db does not exist
        case e:PSQLException => if (e.getMessage.equals(s"""database $db does not exist""")) {/* do nothing */ }
        case e:Throwable => throw e // escalate other exceptions
      }
      finally db.close()
    }
  }

  private def createDbConnection(user: String, maybePassword: Option[String], defaultDbUrl: String):
  PostgresProfile.backend.DatabaseDef = {
    maybePassword.fold(Database.forURL(defaultDbUrl, user = user, driver = jdbcDriver,
      keepAliveConnection = true))(password => Database.forURL(defaultDbUrl, user = user, password = password,
      driver = jdbcDriver, keepAliveConnection = true))
  }
}
