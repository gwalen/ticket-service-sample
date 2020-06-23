package utils

import com.typesafe.config.ConfigFactory
import eventworld.common.database.BaseDb.driver.api._

object ConfigSpec {

  val testConf = ConfigFactory.load("application-test")
  val testDb     = Database.forConfig(path = "h2mem1", config = testConf)

}
