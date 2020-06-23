package eventworld.context.reservation.repository

import java.time.Instant

import eventworld.context.reservation.domian._
import eventworld.common.database.BaseDb.driver.api._
import slick.lifted.{ProvenShape, Tag}

class Reservations(tag: Tag) extends Table[Reservation](tag, Reservations.tableName) {

  def id: Rep[Long]            = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def clientId: Rep[Long]      = column[Long]("client_id")
  def eventId: Rep[Long]       = column[Long]("event_id")
  def ticketCount: Rep[Int]    = column[Int]("ticket_count")
  def expiryDate: Rep[Instant] = column[Instant]("expiry_date")

  override def * : ProvenShape[Reservation] = (id, clientId, eventId, ticketCount, expiryDate) <> (Reservation.tupled, Reservation.unapply)
}

object Reservations {
  val tableName = "reservations"
}
