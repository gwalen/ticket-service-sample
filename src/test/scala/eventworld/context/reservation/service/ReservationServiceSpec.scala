package eventworld.context.reservation.service

import java.time.Instant

import akka.Done
import akka.http.scaladsl.testkit.ScalatestRouteTest
import eventworld.context.reservation.domian.Reservation
import eventworld.context.reservation.domian.ReservationCounter
import eventworld.context.reservation.domian.dto.ReservationCreateRequest
import eventworld.context.reservation.domian.dto.ReservationCreateResponses
import eventworld.context.reservation.domian.dto.ReservationExtendRequest
import eventworld.context.reservation.repository.ReservationRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.dbio.DBIOAction


class ReservationServiceSpec extends AnyFlatSpec with Matchers with MockFactory with ScalaFutures with ScalatestRouteTest { spec =>
  import utils.ConfigSpec._

  override def testConfig = testConf

  it should "return success during reservation when no error" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)
    val eventId = 1000
    val reservationCreateRequest = ReservationCreateRequest(Reservation(10, 100, eventId, 1, Instant.now))
    val reservationCounter = ReservationCounter(eventId, 500, 0, 5)

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(DBIOAction.successful(Option(reservationCounter)))
    (reservationRepositoryStub.insertWithMaxReservationCheck(_: Reservation)).when(*).returns(DBIOAction.successful(1))
    //TODO: fix test --> add stub for db check if client has reservations
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe ReservationCreateResponses.Successful
  }

  it should "return failure during reservation when insert affected rows == 0 (update condition was not met - not enough tickets)" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)
    val eventId = 1000
    val reservationCreateRequest = ReservationCreateRequest(Reservation(10, 100, eventId, 1, Instant.now))
    val reservationCounter = ReservationCounter(eventId, 500, 0, 5)

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(DBIOAction.successful(Option(reservationCounter)))
    (reservationRepositoryStub.insertWithMaxReservationCheck(_: Reservation)).when(*).returns(DBIOAction.successful(0))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe ReservationCreateResponses.NotEnoughTickets
  }

  it should "return failure during reservation when no reservation counter found" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)
    val eventId = 1000
    val reservationCreateRequest = ReservationCreateRequest(Reservation(10, 100, eventId, 1, Instant.now))
    val reservationCounter = ReservationCounter(eventId, 500, 0, 5)

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(DBIOAction.successful(None))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe ReservationCreateResponses.EventReservationsNotFound
  }

  it should "return failure during reservation when clients wants to many tickets" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)
    val eventId = 1000
    val reservationCreateRequest = ReservationCreateRequest(Reservation(10, 100, eventId, 10, Instant.now))
    val reservationCounter = ReservationCounter(eventId, 500, 0, 5)

    (reservationRepositoryStub.findReservationCounter(_: Long)).when(*).returns(DBIOAction.successful(Option(reservationCounter)))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe ReservationCreateResponses.TooManyTicketsForClient
  }

  it should "return correct result when removal was successful" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)

    (reservationRepositoryStub.remove(_: Long)).when(*).returns(DBIOAction.successful(1))
    reservationService.cancelReservation(1).futureValue shouldBe Done
  }

  it should "return correct result when update of expiry was successful" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)
    val reservationExtendRequest = ReservationExtendRequest(1, Instant.now)

    (reservationRepositoryStub.updateReservationExpiryDate(_: Long, _: Instant)).when(*, *).returns(DBIOAction.successful(1))
    reservationService.extendReservation(reservationExtendRequest).futureValue shouldBe Done
  }

  it should "return correct result when find query was successful" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)

    val reservationsFromDb = Seq(Reservation(1, 1, 1, 1, Instant.MAX))

    (reservationRepositoryStub.findAllReservations _).when().returns(DBIOAction.successful(reservationsFromDb))
    reservationService.findReservations().futureValue shouldBe reservationsFromDb
  }

}
