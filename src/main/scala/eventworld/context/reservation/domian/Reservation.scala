package eventworld.context.reservation.domian

import java.time.Instant

case class Reservation(
  id: Long,
  clientId: Long,
  eventId: Long,
  ticketCount: Int,
  expiryDate: Instant
)
