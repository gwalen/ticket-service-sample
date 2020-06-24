package eventworld.context.reservation.router

import akka.Done
import eventworld.context.reservation.domian.Reservation
import eventworld.context.reservation.domian.dto.ReservationCreateRequest
import eventworld.context.reservation.domian.dto.ReservationExtendRequest
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.json.circe._

object ReservationEndpoints {

  val baseEndpoint: Endpoint[Unit, String, Unit, Nothing] = endpoint.errorOut(stringBody).in("reservations")

  val createReservation: Endpoint[ReservationCreateRequest, String, String, Nothing] = baseEndpoint.post
    .in(jsonBody[ReservationCreateRequest])
    .out(jsonBody[String])

  val extendReservation: Endpoint[ReservationExtendRequest, String, Done, Nothing] = baseEndpoint.patch
    .in(jsonBody[ReservationExtendRequest])
    .out(jsonBody[Done])

  val cancelReservation: Endpoint[Long, String, Done, Nothing] = baseEndpoint.delete
    .in(path[Long]("reservationId"))
    .out(jsonBody[Done])

  val findReservations: Endpoint[Unit, String, List[Reservation], Nothing] = baseEndpoint.get
    .out(jsonBody[List[Reservation]])

  val findReservationsForClient: Endpoint[Long, String, List[Reservation], Nothing] = baseEndpoint.get
    .in(path[Long]("clientId"))
    .out(jsonBody[List[Reservation]])
}
