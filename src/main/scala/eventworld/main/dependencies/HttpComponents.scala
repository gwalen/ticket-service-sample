package eventworld.main.dependencies

import com.softwaremill.macwire.wire
import eventworld.context.health.HealthRouter
import eventworld.context.reservation.router.ReservationRouter

trait HttpComponents { self: CommonLayer with BusinessLogicComponents =>
  lazy val healthRouter: HealthRouter = wire[HealthRouter]
  lazy val reservationRouter: ReservationRouter   = wire[ReservationRouter]
}
