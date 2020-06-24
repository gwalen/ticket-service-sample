package eventworld.context.reservation.router

//import akka.http.scaladsl.model.StatusCodes._
//import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import eventworld.context.reservation.domian.Reservation
import eventworld.context.reservation.domian.dto.ReservationCreateRequest
import eventworld.context.reservation.domian.dto.ReservationCreateResponse
import eventworld.context.reservation.domian.dto.ReservationExtendRequest
import eventworld.context.reservation.service.ReservationService
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

object Endpoints {

  val baseEndpoint: Endpoint[Unit, String, Unit, Nothing] = endpoint.errorOut(stringBody).in("reservationsV2")
//  val baseEndpoint: Endpoint[Unit, Unit, Unit, Nothing] = endpoint.in("reservationsV2")

  val createReservation: Endpoint[ReservationCreateRequest, String, ReservationCreateResponse, Nothing] = baseEndpoint.post
//    .in("add")
    .in(jsonBody[ReservationCreateRequest])
    .out(jsonBody[ReservationCreateResponse])

  val findReservations: Endpoint[Unit, String, List[Reservation], Nothing] = baseEndpoint.get
//    .in(yearParameter)
//    .in(limitParameter)
    .out(jsonBody[List[Reservation]])

  val findReservationsForClient: Endpoint[Long, String, List[Reservation], Nothing] = baseEndpoint.get
    .in(path[Long]("clientId"))
    //    .in(limitParameter)
    .out(jsonBody[List[Reservation]])
}

class ReservationRouterTapir(reservationService: ReservationService)(implicit ex: ExecutionContext, mat: Materializer) {
  import Endpoints._

  val a: () => Seq[Reservation] = ???
  val b: () => Future[Seq[Reservation]] = reservationService.findAllReservations _
  val b1: Function0[Future[List[Reservation]]] = reservationService.findAllReservations _
  val b3: Function1[Unit, Future[List[Reservation]]] = reservationService.findAllReservationsUnit _
//  val b2: PartialFunction[Unit, Future[List[Reservation]]] = reservationService.findAllReservations _ //does not complie
  val c: Long => Future[List[Reservation]] = reservationService.findReservations _
  val c1: Function1[Long, Future[List[Reservation]]] = reservationService.findReservations _
//  val c2: PartialFunction[Long, Future[List[Reservation]]] = reservationService.findReservations _ //does not compile

  val bb = new Function0[Future[List[Reservation]]] {
    def apply(): Future[List[Reservation]] = reservationService.findAllReservations
  }


  val d = b3.andThen(handleErrors)
  val d2 = c1.andThen(handleErrors)


  val routes: Route =
//    createReservation.toRoute(reservationService.createReservation(_).map(Right[Unit, ReservationCreateResponse]))
    createReservation.toRoute((reservationService.createReservation _).andThen(handleErrors))
//      ~ findReservations.toRoute((reservationService.findAllReservations).andThen(handleErrors))
//      ~ findReservations.toRoute((reservationService.findAllReservations _).map(Right[Unit, Seq[Reservation]]))
//      ~ findReservations.toRoute((b).andThen(handleErrors))
//      ~ findReservations.toRoute((a.apply _).map(Right[Unit, Seq[Reservation]]))

  val rrr: Route = findReservationsForClient.toRoute((reservationService.findReservations _).andThen(handleErrors[List[Reservation]]))
  val rrr2: Route = findReservations.toRoute((reservationService.findAllReservationsUnit _).andThen(handleErrors))
//  val rrr3: Route = findReservations.toRoute( handleErrors[List[Reservation]](b1()) _)

  /*
  val routes: Route =
    pathPrefix("reservations") {
      (post & pathEndOrSingleSlash & entity(as[ReservationCreateRequest]) ) { request =>
        //TODO: map to extedend message with 402 (BadRequest messages on error)
        complete(reservationService.createReservation(request).map(OK -> _))
      } ~ (patch & pathEndOrSingleSlash & entity(as[ReservationExtendRequest])) { request =>
        complete(reservationService.extendReservation(request).map(OK -> _))
      } ~ (get & pathEndOrSingleSlash ) {
        complete(reservationService.findReservations().map(OK -> _))
      } ~ (get & path("events" / LongNumber) & pathEndOrSingleSlash ) { eventId =>
        complete(reservationService.findReservations(eventId).map(OK -> _))
      } ~ (delete & path(Segment) & pathEndOrSingleSlash) { reservationId =>
        complete(reservationService.cancelReservation(reservationId.toLong).map(OK -> _))
      }
    }
    */

  private def handleErrors[T](f: Future[T]): Future[Either[String, T]] =
    f.transform {
      case Success(v) => Success(Right(v))
      case Failure(e) =>
        //        logger.error("Exception when running endpoint logic", e)
        Success(Left(e.getMessage))
    }
}