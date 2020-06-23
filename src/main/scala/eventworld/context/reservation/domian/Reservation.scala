package eventworld.context.reservation.domian

import java.time.Instant

//TODO: expiry date powinno byc ustwiane w wenetrznej logice
// id nie powinno isc w reuest (post)
// zrob dto

case class Reservation(
  id: Long,
  clientId: Long,
  eventId: Long,
  ticketCount: Int,
  expiryDate: Instant
)
