package eventworld.common.database

import com.github.tminglei.slickpg._
import slick.basic.Capability

trait PostgresDriver extends ExPostgresProfile with PgSprayJsonSupport with PgDate2Support with PgArraySupport with PgEnumSupport {

  override val pgjson = "jsonb"

  override val api = PostgresApi

  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  object PostgresApi
      extends API
      with JsonImplicits
      with SprayJsonPlainImplicits
      with ArrayImplicits
      with DateTimeImplicits

}

object PostgresDriver extends PostgresDriver
