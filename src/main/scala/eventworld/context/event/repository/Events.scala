package eventworld.context.event.repository

import eventworld.common.database.BaseDb.driver.api._
import eventworld.context.event.domain.Event
import slick.lifted.Tag

class Events(tag: Tag) extends Table[Event](tag, "tickets") {

  def id: Rep[Long]                 = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[String]             = column[String]("name")
  def ticketCount: Rep[Long]        = column[Long]("ticket_count")
  def maxTicketsForClient: Rep[Int] = column[Int]("max_tickets_for_client")

  override def * = (id, name, ticketCount, maxTicketsForClient) <> (Event.tupled, Event.unapply)
}
