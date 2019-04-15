import java.io.File

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.sofichallenge.transactionapi.service.{PingServlet, TransactionServlet}
import com.typesafe.config.ConfigFactory
import javax.servlet.ServletContext
import org.scalatra._
import org.slf4j.LoggerFactory
import com.sofichallenge.transactionapi.bootstrap.BootstrapConfigHelper
import com.sofichallenge.transactionapi.bootstrap.BootstrapConfigHelper._
import com.sofichallenge.transactionapi.database.tablemapping.PostgresTablesWithDb
import com.sofichallenge.transactionapi.dependency.impl.TransactionApiDBImpl
import com.sofichallenge.transactionapi.handler.TransactionRequestHandler

import scala.concurrent.ExecutionContext.Implicits.global

class ScalatraBootstrap extends LifeCycle with BootstrapConfigHelper {
  val logger = LoggerFactory.getLogger(getClass)
  val baseConf = ConfigFactory.load()
  val fileLocation = "override/local.properties"
  val overrideConf =  ConfigFactory.parseFile(new File(fileLocation))
  val conf = ConfigFactory.load(overrideConf).withFallback(baseConf)
  val cpds = new ComboPooledDataSource
  val env = getFromSysOrConf(scalatraEnvKey, conf)

  cpds.setMaxPoolSize(conf.getInt("c3p0.maxPoolSize"))
  cpds.setJdbcUrl(getFromSysOrConf(jdbcUrlKey, conf))
  cpds.setUser(getFromSysOrConf(dbUserKey, conf))
  cpds.setPassword(getFromSysOrConf(dbPasswordKey, conf))
  val tablesWithDb = new PostgresTablesWithDb(cpds)

  logger.info("Created c3p0 connection pool and db")

  override def init(context: ServletContext) {
    val txApi = new TransactionApiDBImpl(tablesWithDb)
    val txHandler = new TransactionRequestHandler(txApi)

    context.mount(new TransactionServlet(txHandler), "/*")
    context.mount(new PingServlet, "/*")
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection()
  }

  private def closeDbConnection() {
    logger.info("Closing c3po connection pool and slick resources")
    tablesWithDb.db.close()
    cpds.close()
  }
}
