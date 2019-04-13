lazy val transaction_sbt = (project in file(".")).settings(
  name := "transaction-sbt",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.3",
    "com.iheart" %% "ficus" % "1.4.5",
    "com.typesafe.slick" %% "slick" % "3.3.0",
    "org.postgresql" % "postgresql" % "42.2.5",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
)