package com.transactionapi.sbt

/**
  *
  */
case class SbtDbParams(dbUrl: String, dbUser: String, dbPassword: Option[String], dbFlywayTable: Option[String])
