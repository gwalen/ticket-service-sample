package eventworld.main.dependencies

import com.softwaremill.macwire.wire
import eventworld.context.reservation.repository.ReservationRepository

trait DatabaseComponents { self: CommonLayer =>

  lazy val titleRepository: ReservationRepository = wire[ReservationRepository]
}
