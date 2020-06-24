package eventworld.main.dependencies

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import eventworld.common.database.BaseDb.driver.api._
import eventworld.main.config._

import scala.concurrent.ExecutionContext

trait CommonLayer { self =>

  implicit val system: ActorSystem
  implicit def executor: ExecutionContext

  lazy val config: Config                  = ConfigFactory.load("application-env")
  implicit lazy val logger: LoggingAdapter = system.log

  lazy val db: Database = Database.forConfig("db", config)

  lazy val dbConfig = DatabaseConfig(
    config.getString("db.url"),
    config.getString("db.user"),
    config.getString("db.password"),
    config.getBoolean("db.flyway-migration-during-boot"))

  lazy val serverConfig: ServerConfig = ServerConfig(
    config.getString("http.interface"),
    config.getInt("http.port"),
    config.getString("http.hostname")
  )

}
