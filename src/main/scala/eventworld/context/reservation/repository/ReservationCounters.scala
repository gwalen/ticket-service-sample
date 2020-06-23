package eventworld.context.reservation.repository

import eventworld.common.database.BaseDb.driver.api._
import eventworld.context.reservation.domian._
import slick.lifted.ProvenShape
import slick.lifted.Tag

class ReservationCounters(tag: Tag) extends Table[ReservationCounter](tag, ReservationCounters.tableName) {

  def eventId: Rep[Long]         = column[Long]("event_id", O.PrimaryKey)
  def maxTickets: Rep[Long]      = column[Long]("max_tickets")
  def reservedTickets: Rep[Long] = column[Long]("reserved_tickets")

  override def * : ProvenShape[ReservationCounter] =
    (eventId, maxTickets, reservedTickets) <> ((ReservationCounter.apply _).tupled, ReservationCounter.unapply)
}

object ReservationCounters {
  val tableName = "reservation_counters"
}
