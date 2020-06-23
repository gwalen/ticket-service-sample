package eventworld.context.reservation.router

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import eventworld.context.reservation.domian.dto.ReservationCreateRequest
import eventworld.context.reservation.domian.dto.ReservationExtendRequest
import eventworld.context.reservation.service.ReservationService
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._


import scala.concurrent.ExecutionContext

class ReservationRouter(reservationService: ReservationService)(implicit ex: ExecutionContext, mat: Materializer) {

  val routes: Route =
    pathPrefix("reservations") {
      (post & pathEndOrSingleSlash & entity(as[ReservationCreateRequest]) ) { request =>
        complete(reservationService.createReservation(request).map(OK -> _))
      } ~ (put & pathEndOrSingleSlash & entity(as[ReservationExtendRequest])) { request =>
        complete(reservationService.extendReservation(request).map(OK -> _))
      } ~ (get & pathEndOrSingleSlash ) {
        complete(reservationService.findReservations().map(OK -> _))
      } ~ (get & path("events" / LongNumber) & pathEndOrSingleSlash ) { eventId =>
        complete(reservationService.findReservations(eventId).map(OK -> _))
      } ~ (delete & path(Segment) & pathEndOrSingleSlash) { reservationId =>
        complete(reservationService.cancelReservation(reservationId.toLong).map(OK -> _))
      }
    }
}