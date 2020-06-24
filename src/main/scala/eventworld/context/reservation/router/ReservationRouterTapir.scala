package eventworld.context.reservation.router

import akka.Done
//import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import eventworld.context.reservation.service.ReservationService
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext
import sttp.tapir.server.akkahttp._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success


class ReservationRouterTapir(reservationService: ReservationService)(implicit ex: ExecutionContext, mat: Materializer) {
  import ReservationEndpointTapir._

  val routes: Route =
    createReservation.toRoute((reservationService.createReservation _).andThen(_.map(_.toString)).andThen(handleErrors)) ~
    findReservations.toRoute((reservationService.findAllReservationsUnit _).andThen(handleErrors)) ~
    findReservationsForClient.toRoute((reservationService.findReservations _).andThen(handleErrors)) ~
    extendReservation.toRoute((reservationService.extendReservation _).andThen(handleErrors[Done])) ~
    cancelReservation.toRoute((reservationService.cancelReservation _).andThen(handleErrors[Done]))

  val docsRoutes: Route = new SwaggerAkka(openapiYamlDocumentation).routes

  val routesWithDocs: Route = routes ~ docsRoutes

  private def handleErrors[T](f: Future[T]): Future[Either[String, T]] =
    f.transform {
      case Success(v) => Success(Right(v))
      case Failure(e) =>
        //        logger.error("Exception when running endpoint logic", e)
        Success(Left(e.getMessage))
    }

  def openapiYamlDocumentation: String = {
    import sttp.tapir.docs.openapi._
    import sttp.tapir.openapi.circe.yaml._

    // interpreting the endpoint description to generate yaml openapi documentation
    val docs = List(
      createReservation,
      findReservationsForClient,
      findReservations,
      extendReservation,
      cancelReservation
    ).toOpenAPI("Ticket reservations", "1.0")
    docs.toYaml
  }
}