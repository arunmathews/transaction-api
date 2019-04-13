import java.io.File

import com.sofichallenge.transactionapi.service.{PingServlet, TransactionListServlet}
import com.typesafe.config.ConfigFactory
import javax.servlet.ServletContext
import org.scalatra._
import org.slf4j.LoggerFactory

class ScalatraBootstrap extends LifeCycle {
  val logger = LoggerFactory.getLogger(getClass)
  val baseConf = ConfigFactory.load()
  val fileLocation = "override/local.properties"
  val overrideConf =  ConfigFactory.parseFile(new File(fileLocation))
  val conf = ConfigFactory.load(overrideConf).withFallback(baseConf)

  override def init(context: ServletContext) {
    context.mount(new TransactionListServlet, "/*")
    context.mount(new PingServlet, "/*")
  }
}
