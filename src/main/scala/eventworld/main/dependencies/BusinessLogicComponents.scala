package eventworld.main.dependencies

import com.softwaremill.macwire.wire
import eventworld.context.reservation.service.ReservationService

trait BusinessLogicComponents { self: CommonLayer with DatabaseComponents =>

  lazy val titleService: ReservationService = wire[ReservationService]
}
