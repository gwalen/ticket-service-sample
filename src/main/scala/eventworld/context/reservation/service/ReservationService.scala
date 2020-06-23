package eventworld.context.reservation.service

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.Materializer
import eventworld.common.database.BaseDb.driver.api._
import eventworld.context.reservation.domian._
import eventworld.context.reservation.domian.dto.CreateReservationFailed
import eventworld.context.reservation.domian.dto.CreateReservationSuccessful
import eventworld.context.reservation.domian.dto.ReservationCreateRequest
import eventworld.context.reservation.domian.dto.ReservationCreateResponse
import eventworld.context.reservation.domian.dto.ReservationExtendRequest
import eventworld.context.reservation.repository.ReservationRepository

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

//TODO: rename packages:
// router -> restapi
// service -> application
// repository -> persistance (?)

//TODO: akka error handler (500)

class ReservationService(
  reservationRepository: ReservationRepository,
  db: Database
)(implicit ec: ExecutionContext, mat: Materializer, system: ActorSystem) {

  private val logger = Logging(system, getClass)

  def createReservation(request: ReservationCreateRequest): Future[ReservationCreateResponse] = {
    logger.info(s"Create reservation for: $request")
    //TODO: check max number of tickets for user not exceeded

    db.run(reservationRepository.insertWithMaxReservationCheck(request.reservation))
      .map {
        case rowsAffected if rowsAffected == 0 => CreateReservationFailed
        case rowsAffected if rowsAffected > 0  => CreateReservationSuccessful
      }
  }

  def extendReservation(request: ReservationExtendRequest): Future[Done] = {
    logger.info(s"Extend reservation for: $request")
    db.run(reservationRepository.updateReservationExpiryDate(request.reservationId, request.newExpiryDate)).map(_ => Done)
  }

  def cancelReservation(reservationId: Long): Future[Done] = {
    logger.info(s"Cancel reservation : $reservationId")
    db.run(reservationRepository.remove(reservationId)).map(_ => Done)
  }

  def findReservations(): Future[Seq[Reservation]] = {
    logger.info(s"Get all reservations")
    db.run(reservationRepository.findAllReservations())
  }

  def findReservations(eventId: Long): Future[Seq[Reservation]] = {
    logger.info(s"Get all reservations for event = $eventId")
    db.run(reservationRepository.findAllReservationsForEvent(eventId))
  }

}
