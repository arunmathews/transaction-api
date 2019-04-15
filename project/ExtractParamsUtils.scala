import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._

/**
  * Utility class for extracting params from conf files
  */
object ExtractParamsUtils {

    def extractDbParamsFromFile(args: Seq[String], defaultPropertiesPath: String): SbtDbParams = {

      val filePath = if (args.isEmpty) {
        println(s"File path not provided, using default path: $defaultPropertiesPath")
        defaultPropertiesPath
      }
      else {
        args.head
      }
      val conf = ConfigFactory.parseFile(new File(filePath))
      val maybeUrl = conf.as[Option[String]]("db_jdbcUrl")
      val maybeUser = conf.as[Option[String]]("db_user")
      val maybePassword = conf.as[Option[String]]("db_password")
      val maybeFlywayTable = conf.as[Option[String]]("db_flyway_table")
      (maybeUrl, maybeUser, maybePassword, maybeFlywayTable) match {
        case (Some(url), Some(user), password, dbTable) =>
          SbtDbParams(url, user, password, dbTable)
        case _ =>
          throw new RuntimeException(s"db_jdbcUrl, db_user and/or db_password is missing in file at path: $filePath")
      }
    }

    def getFromSysOrConf(confKey: String, conf: Config): String = sys.env.getOrElse(confKey, conf.getString(confKey))

    def getFromSysOrConfOpt(confKey: String, conf: Config): Option[String] = sys.env.get(confKey).orElse(conf.as[Option[String]](confKey))
}
