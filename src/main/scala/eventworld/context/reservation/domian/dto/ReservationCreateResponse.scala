package eventworld.context.reservation.domian.dto

//TODO: raname to  reservation ReservationCreateResult
// create ReservationCreateResponse(result: String) which will be mapped from ReservationCreateResult
sealed trait ReservationCreateResponse

object ReservationCreateResponses {
  case object Successful                          extends ReservationCreateResponse
  case object NotEnoughTickets                    extends ReservationCreateResponse
  case object TooManyTicketsForClient             extends ReservationCreateResponse
  case object EventReservationsNotFound           extends ReservationCreateResponse
  case object ClientAlreadyHasReservationForEvent extends ReservationCreateResponse
}
