package eventworld.context.reservation.service

import java.time.Instant

import akka.Done
import akka.http.scaladsl.testkit.ScalatestRouteTest
import eventworld.context.reservation.domian.Reservation
import eventworld.context.reservation.domian.dto.CreateReservationFailed
import eventworld.context.reservation.domian.dto.CreateReservationSuccessful
import eventworld.context.reservation.domian.dto.ReservationCreateRequest
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

  it should "return success when on insert affected rows > 1" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)
    val reservationCreateRequest = ReservationCreateRequest(Reservation(10, 100, 1000, 1, Instant.now))

    (reservationRepositoryStub.insertWithMaxReservationCheck(_: Reservation)).when(*).returns(DBIOAction.successful(1))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe CreateReservationSuccessful
  }

  it should "return failure when on insert affected rows == 0 (update condition was not met)" in {
    val reservationRepositoryStub = stub[ReservationRepository]
    val reservationService = new ReservationService(reservationRepositoryStub, testDb)
    val reservationCreateRequest = ReservationCreateRequest(Reservation(10, 100, 1000, 1, Instant.now))

    (reservationRepositoryStub.insertWithMaxReservationCheck(_: Reservation)).when(*).returns(DBIOAction.successful(0))
    reservationService.createReservation(reservationCreateRequest).futureValue shouldBe CreateReservationFailed
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
