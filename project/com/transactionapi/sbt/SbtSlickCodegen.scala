package com.transactionapi.sbt

import java.io.File

import sbt.Keys._
import sbt.{Def, _}

import scala.util.{Failure, Success}

object SbtSlickCodegen extends AutoPlugin {

  lazy val genScalaTables = taskKey[Seq[File]]("Generate Tables.scala in generated sources " +
    "that reflects current db schema")

  def slickCodeGenTask(dbHostUrl: String, user: String, password: Option[String], dbName: String,
                       sourceCodeGenClassPath: String, slickDriverPath: String, dbGenSourcePkg: String,
                       dbGenSourceDir: String):
  Def.Initialize[Task[Seq[File]]] = Def.task {
    val dir = sourceManaged.value
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
    val outputDir = (dir / "main" / "scala").getPath
    val jdbcDriver = "org.postgresql.Driver"
    val pkg = dbGenSourcePkg

    val res = r.run(sourceCodeGenClassPath, cp.files,
      Seq(slickDriverPath, jdbcDriver, url, outputDir, pkg, user, password.getOrElse(""), "true", sourceCodeGenClassPath), s.log)
    res match {
      case Failure(e) =>
        sys.error(e.toString)
      case Success(_) =>
        ()
    }
    val fname = s"$outputDir/$dbGenSourceDir/Tables.scala"
    Seq(file(fname))
  }
}

