package eventworld.context.reservation.repository

import java.time.Instant

import akka.Done
import eventworld.common.database.BaseDb.driver
import eventworld.common.database.BaseDb.driver.api._
import eventworld.context.reservation.domian._
import slick.lifted.TableQuery
import slick.dbio.Effect
import slick.dbio.Effect
import slick.dbio.Effect
import slick.dbio.Effect
import slick.dbio.Effect
import slick.dbio.Effect
import slick.dbio.Effect
import slick.sql.FixedSqlAction
import slick.sql.FixedSqlStreamingAction
import slick.sql.SqlAction

import scala.concurrent.ExecutionContext

class ReservationRepository()(implicit ec: ExecutionContext) {

  private val reservations        = TableQuery[Reservations]
//  private val reservationCounters = TableQuery[ReservationCounters] //TODO: remove

  /**
   * creates new reservation if max number of ticket per event is not exceeded.
   * to make reservations atomic with max reserved ticket number check we have an extra table reservation_counters
   */
//  def insertWithMaxReservationCheck(reservation: Reservation): DBIOAction[Int, NoStream, Effect.Write] = {
  def insertWithMaxReservationCheck(reservation: Reservation): DBIOAction[Int, NoStream, Effect with Effect.Write with Effect.Transactional] = {
    (for {
      affected <- updateWithCounterIncrementQuery(reservation.eventId, reservation.ticketCount)
      _        <- reservations += reservation
    } yield affected).transactionally
  }

  def remove(reservationId: Long): DBIOAction[Int, NoStream, Effect.Read with Effect with Effect.Write] = {
    for {
      reservationToCancel <- reservations.filter(_.id === reservationId).result.head
      affected            <- updateWithCounterIncrementQuery(reservationToCancel.eventId, -reservationToCancel.ticketCount)
      _                   <- reservations.filter(_.id === reservationId).delete
    } yield affected
  }

  def updateReservationExpiryDate(reservationId: Long, newExpiryDate: Instant): DBIOAction[Int, NoStream, Effect.Write] =
    reservations.filter(_.id === reservationId).map(_.expiryDate).update(newExpiryDate)

  def findAllReservations(): DBIOAction[Seq[Reservation], NoStream, Effect.Read] =
    reservations.result.map(identity(_))

  def findAllReservationsForEvent(eventId: Long): DBIOAction[Seq[Reservation], NoStream, Effect.Read] =
    reservations.filter(_.eventId === eventId).result.map(identity(_))

  private def updateWithCounterIncrementQuery(eventId: Long, ticketsToReserve: Long): SqlAction[Int, NoStream, Effect] =
    sqlu"""update table ${ReservationCounters.tableName} as r
             where r.event_id = $eventId && r.max_tickets < r.reserved_tickets + $ticketsToReserve
             set r.reserved_tickets = r.reserved_tickets + $ticketsToReserve
            """
}
