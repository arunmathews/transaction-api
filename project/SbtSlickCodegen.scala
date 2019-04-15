import java.io.File

import sbt.Keys._
import sbt.{Def, _}

import scala.util.{Failure, Success}

/**
  * Generate scala code for writing to tables using Slick from sbt
  */
object SbtSlickCodegen extends AutoPlugin {

  lazy val genScalaTables = taskKey[Seq[File]]("Generate Tables.scala in generated sources " +
    "that reflects current db schema")

  def slickCodeGenTask(dbHostUrl: String, user: String, password: Option[String], dbName: String,
                       sourceCodeGenClassPath: String, slickDriverPath: String, dbGenSourcePkg: String,
                       dbGenSourceDir: String):
  Def.Initialize[Task[Seq[File]]] = Def.task {
    
    val dir = (Compile / scalaSource).value
    val r = (Compile / runner).value
    val cp = (Compile / dependencyClasspath).value
    val s = streams.value
    generateTablesFile(dbHostUrl, user, password, dbName, sourceCodeGenClassPath, slickDriverPath,
      dbGenSourcePkg, dbGenSourceDir, dir, r, cp, s)
  }

  private def generateTablesFile(dbHostUrl: String, user: String, password: Option[String], dbName: String,
                                 sourceCodeGenClassPath: String, slickDriverPath: String, dbGenSourcePkg: String,
                                 dbGenSourceDir: String, dir: File, r: ScalaRun, cp: Keys.Classpath,
                                 s: Keys.TaskStreams): Seq[File] = {
    val url = s"$dbHostUrl/$dbName"
    val outputDir = dir.getPath
    val jdbcDriver = "org.postgresql.Driver"
    val pkg = dbGenSourcePkg

    val res = r.run(sourceCodeGenClassPath, cp.files,
      Seq(slickDriverPath, jdbcDriver, url, outputDir, pkg, user, password.getOrElse("")), s.log)
    res match {
      case Failure(e) =>
        val st = e.getStackTrace()

        println(s"Got error when generating file with message ${e.getMessage}\n" )
        println(s"Got error when generating file with cause ${e.getCause}\n" )
        sys.error(e.toString)
      case Success(_) =>
        ()
    }
    val fname = s"$outputDir/$dbGenSourceDir/Tables.scala"
    Seq(file(fname))
  }
}
