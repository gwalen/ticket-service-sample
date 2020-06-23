package eventworld.main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.flywaydb.core.Flyway
import eventworld.main.dependencies._

import scala.concurrent.ExecutionContext

trait Setup
    extends HttpComponents
    with BusinessLogicComponents
    with DatabaseComponents
    with CommonLayer {

  lazy val apiRoutes: Route =
    pathPrefix("api") {
      healthRouter.routes ~
      reservationRouter.routes
    }
}

object Boot extends App with Setup {

  override implicit val system: ActorSystem             = ActorSystem("eventworld", config)
  override implicit val executor: ExecutionContext      = system.dispatcher
  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  // Apply database migration
  if (dbConfig.flywayMigrationDuringBoot) {
    val flyway = Flyway.configure().dataSource(dbConfig.url, dbConfig.user, dbConfig.password).load()
    flyway.migrate()
  }

  Http().bindAndHandle(apiRoutes, serverConfig.interface, serverConfig.port)
}
