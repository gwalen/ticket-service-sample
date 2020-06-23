package eventworld.context.reservation.domian.dto

sealed trait ReservationCancelResponse

case object CancelReservationFailed     extends ReservationCancelResponse
case object CancelReservationSuccessful extends ReservationCancelResponse
