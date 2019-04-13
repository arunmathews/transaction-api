import org.scalatra.sbt.ScalatraPlugin
import BuildTasks._
import com.transactionapi.sbt.SbtFlywayMigrateDb._
import com.transactionapi.sbt.SbtSlickCodegen._
import com.transactionapi.sbt.SbtDatabaseTasks._
import com.transactionapi.sbt.SbtTestWithDb._
import sbt.Keys._
import com.earldouglas.xwp.JettyPlugin.autoImport.Jetty

name := "TransactionApiRoot"

version := "0.1.0"

lazy val commonSettings = Seq(
  version := "0.1.0",
  organization := "com.sofichallenge",
  scalaVersion := "2.12.7",
  scalacOptions in Compile ++= Seq("-unchecked", "-Xcheckinit"),
  test in assembly := {},
  assembly / assemblyExcludedJars := {
    val cp = (assembly / fullClasspath).value
    cp filter {_.data.getName == "slf4j-nop-1.6.4.jar"}
  },
  assembly / assemblyMergeStrategy := {
    case PathList("mime.types") =>
      MergeStrategy.last
    case "logback.xml" =>
      MergeStrategy.discard
    case x =>
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
  }
)

val ScalatraVersion = "2.6.+"
val SlickVersion = "3.3.0"
val Json4sVersion = "3.6.5"
val EnumeratumVersion = "1.5.13"
val FlywayVersion = "5.2.4"
val PostgresVersion = "42.2.5"
val JodaVersion = "2.10.1"
val LogbackVersion = "1.2.3"

lazy val root = project.in(file(".")).aggregate(service, flyway)

lazy val service = project.in(file("service")).settings(
  commonSettings,
  ScalatraPlugin.scalatraSettings ++ Seq(
    resolvers ++= Seq(
      Classpaths.typesafeReleases,
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      "RoundEights" at "http://maven.spikemark.net/roundeights"
    ),
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      ("org.scalatra" %% "scalatra-commands" % ScalatraVersion).exclude("commons-collections", "commons-collections"),
      "org.scalatra" %% "scalatra-json" % ScalatraVersion,
      //JSON
      "org.json4s" % "json4s-core_2.12" % Json4sVersion,
      "org.json4s" % "json4s-jackson_2.12" % Json4sVersion,
      "org.json4s" % "json4s-ext_2.12" % Json4sVersion,
      //Configuration
      "com.typesafe" % "config" % "1.3.3",
      "com.iheart" %% "ficus" % "1.4.5",
      //Database
      "com.typesafe.slick" %% "slick" % SlickVersion,
      "com.typesafe.slick" %% "slick-codegen" % SlickVersion,
      "com.mchange" % "c3p0" % "0.9.5.4",
      "org.postgresql" % "postgresql" % PostgresVersion,
      "org.flywaydb" % "flyway-core" % FlywayVersion,
      //Datetime
      "joda-time" % "joda-time" % JodaVersion,
      //General utils
      "com.beachape" %% "enumeratum" % EnumeratumVersion,
      "com.beachape" %% "enumeratum-json4s" % EnumeratumVersion,
      "com.google.guava" % "guava" % "27.1-jre",
      //Test
      "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
      "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
      "org.scalatest" %% "scalatest" % "3.0.7" % "test",
      //Runtime
      "ch.qos.logback" % "logback-classic" % LogbackVersion % "runtime",
      "org.eclipse.jetty" % "jetty-webapp" % "9.2.10.v20150310" % "container",
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
    ),
    containerPort in Jetty := 9080,
    javaOptions ++= Seq(
      "-Dcom.sun.security.enableAIAcaIssuers=true",
      "-Xdebug",
      "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9005"
    ),
    // register manual slick codegen sbt command
    genScalaTables := slickCodeGenTask(dbHostUrl, dbDefaultUser, dbDefaultPassword, commonDb,
      slickCodeGeneratorFQCN, slickPostgresDriverFQCN, dbGenSourcePkg, dbGenSourceDir).value,
// register automatic code generation on every compile, remove for only manual use
    Compile / sourceGenerators += genScalaTables.toTask.taskValue
  )
).enablePlugins(ScalatraPlugin)

lazy val flyway = (project in file("flyway"))
  .settings(commonSettings,
    Seq(
      name := "flyway",
      assembly / assemblyJarName := "flyway-assembly.jar",
      resolvers ++= Seq(
        Classpaths.typesafeReleases,
        "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
        "RoundEights" at "http://maven.spikemark.net/roundeights"
      ),
      dependencyOverrides := Seq(
        "org.scala-lang" % "scala-library" % scalaVersion.value,
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "org.scala-lang" % "scala-compiler" % scalaVersion.value
      ),
      libraryDependencies ++= Seq(
        "org.postgresql" % "postgresql" % PostgresVersion,
        "org.flywaydb" % "flyway-core" % FlywayVersion,
        //Logging
        "ch.qos.logback" % "logback-classic" % LogbackVersion,
        //date
        "joda-time" % "joda-time" % JodaVersion
      ),
      //register manual flyway db migrate command
      migrateFlywayDbTables := (migrateFlywayDbTask(defaultPropertiesPath, dbFlywayLocation)).evaluated,
      setupCommonDb := createCommonDbAndTablesTask.value,
      Test / setupTestDb := createTestDbAndTablesTask.value,
      Test / teardownTestDb := (dropDbInputTask(dbHostUrl, dbDefaultUser, dbDefaultPassword, defaultDb,
        testDb)).evaluated
    )
  ).enablePlugins(FlywayPlugin)
