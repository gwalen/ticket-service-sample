package eventworld.common.database

object BaseDb {
  val driver = PostgresDriver
  type Database = driver.api.Database
}
