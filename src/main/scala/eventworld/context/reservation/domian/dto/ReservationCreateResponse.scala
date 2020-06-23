package eventworld.context.reservation.domian.dto

sealed trait ReservationCreateResponse

case object CreateReservationFailed     extends ReservationCreateResponse
case object CreateReservationSuccessful extends ReservationCreateResponse
