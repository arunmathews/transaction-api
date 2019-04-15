package com.sofichallenge.transactionapi.bootstrap

import com.typesafe.config.Config

trait BootstrapConfigHelper {
  def getFromSysOrConf(confKey: String, conf: Config): String = sys.env.getOrElse(confKey, conf.getString(confKey))
}

object BootstrapConfigHelper {
  val scalatraEnvKey = "scalatra_environment"
  val jdbcUrlKey = "db_jdbcUrl"
  val dbUserKey = "db_user"
  val dbPasswordKey = "db_password"
}
