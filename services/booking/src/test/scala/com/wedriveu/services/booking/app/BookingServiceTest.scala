package com.wedriveu.services.booking.app

import com.wedriveu.services.booking.entity.{BookingStore, BookingStoreImpl}
import com.wedriveu.services.booking.util.{ConsumerConfig, RabbitMQConsumers}
import com.wedriveu.services.shared.model.Booking
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory
import com.wedriveu.services.shared.store.{EntityListStoreStrategy, JsonFileEntityListStoreStrategyImpl}
import com.wedriveu.services.shared.vertx.VertxJsonMapper
import com.wedriveu.shared.rabbitmq.message._
import com.wedriveu.shared.util.{Log, Position, Constants => Shared}
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.Vertx
import io.vertx.scala.core.eventbus.{EventBus, Message}
import io.vertx.scala.rabbitmq.RabbitMQClient
import org.junit.runner.RunWith
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * @author Nicola Lasagni on 19/08/2017.
  */
@RunWith(classOf[VertxUnitRunner])
class BookingServiceTest extends AssertionsForJUnit {

  private val UserSimulatedQueue = this.getClass.getCanonicalName + "UserQueue"
  private val UserSimulatedEventBusAddress = this.getClass.getCanonicalName + "UserEventBus"
  private val VehicleServiceSimulatedQueue = this.getClass.getCanonicalName + "VehicleServiceQueue"
  private val VehicleServiceSimulatedEventBusAddress = this.getClass.getCanonicalName + "VehicleServiceEventBus"

  private val Username = "Pippo"
  private val LicensePlate = "ABCDEFGHI"
  private val NewLicensePlate = "NewLicensePlate"
  private val UserPosition = new Position(42.1, 12.3)
  private val DestinationPosition = new Position(43.1, 11.3)
  private val DriveTimeToUser = 1000 * 60 * 60 * 4
  private val DriveTimeToDestination = 1000 * 60 * 60 * 8

  private val fileName: String = "bookings.json"
  private val storeStrategy: EntityListStoreStrategy[Booking] =
    new JsonFileEntityListStoreStrategyImpl[Booking](classOf[Booking], fileName)
  private val store: BookingStore = new BookingStoreImpl(storeStrategy)

  private var eventBus: EventBus = _
  private var client: RabbitMQClient = _

  @Before def setUp(context: TestContext): Unit = {
    val vertx = Vertx.vertx()
    eventBus = vertx.eventBus()
    client = RabbitMQClient.create(vertx, RabbitMQClientFactory.createClientConfig())
    val async = context.async()
    client.startFuture().flatMap(_ => {
      vertx.deployVerticleFuture(ServiceBoot.Verticle)
    }).flatMap(_ => {
      simulateVehicleService()
    })onComplete {
      case Success(_) => async.complete()
      case Failure(result) => context.fail(result.getCause)
    }
  }

  private def simulateVehicleService(): Future[_] = {
    val response = new BookVehicleResponse
    response.setBooked(true)
    response.setLicensePlate(LicensePlate)
    response.setDriveTimeToUser(DriveTimeToUser)
    response.setDriveTimeToDestination(DriveTimeToDestination)
    BookingServiceTestSimulation.simulateVehicleService(
      client,
      eventBus,
      VehicleServiceSimulatedQueue,
      VehicleServiceSimulatedEventBusAddress,
      response)
  }

  @After def tearDown(context: TestContext) {
    client.stopFuture()
  }

  @Test def createBooking(context: TestContext) {
    val async = context.async()
    val queue = UserSimulatedQueue + "CreateBooking"
    val eventBusAddress = UserSimulatedEventBusAddress + "CreateBooking"
    eventBus.consumer(eventBusAddress, (message: Message[Object]) => message.body() match {
      case body: JsonObject =>
        //TODO
        Log.info(this.getClass.getSimpleName, "Received CreateBookingResponse")
        val response: CreateBookingResponse = VertxJsonMapper.mapFromBodyTo(body, classOf[CreateBookingResponse])
        context.assertTrue(response != null)
        async.complete()
      case _ => context.fail(); async.complete()
    })
    val config = ConsumerConfig(
      queue,
      durableQueue = false,
      Shared.RabbitMQ.Exchanges.BOOKING,
      String.format(Shared.RabbitMQ.RoutingKey.CREATE_BOOKING_RESPONSE, Username),
      eventBusAddress
    )
    RabbitMQConsumers.createConsumer(client, config).flatMap(_ => {
      client.basicPublishFuture(
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.CREATE_BOOKING_REQUEST,
        VertxJsonMapper.mapInBodyFrom(createDummyCreateBookingRequest()))
    })
    async.awaitSuccess()
  }

  private def createDummyCreateBookingRequest(): CreateBookingRequest = {
    val request = new CreateBookingRequest
    request.setUsername(Username)
    request.setLicensePlate(LicensePlate)
    request.setUserPosition(UserPosition)
    request.setDestinationPosition(DestinationPosition)
    request
  }

  @Test def changeBooking(context: TestContext): Unit = {
    val async = context.async(2)
    val queue = VehicleServiceSimulatedQueue + "ChangeBooking"
    val eventBusAddress = VehicleServiceSimulatedEventBusAddress + "ChangeBooking"
    eventBus.consumer(eventBusAddress, (message: Message[Object]) => message.body() match {
      case body: JsonObject =>
        val response: ChangeBookingResponse = VertxJsonMapper.mapFromBodyTo(body, classOf[ChangeBookingResponse])
        context.assertTrue(response != null && (!response.isSuccess || response.getLicencePlate.equals(NewLicensePlate)))
        async.complete()
      case _ => context.fail(); async.complete()
    })
    val config = ConsumerConfig(
      queue,
      durableQueue = false,
      Shared.RabbitMQ.Exchanges.BOOKING,
      Shared.RabbitMQ.RoutingKey.CHANGE_BOOKING_RESPONSE,
      eventBusAddress
    )
    RabbitMQConsumers.createConsumer(client, config).flatMap(_ => {
      client.basicPublishFuture(
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.CHANGE_BOOKING_REQUEST,
        VertxJsonMapper.mapInBodyFrom(createDummyChangeBookingRequest()))
    }).onComplete {
      case Success(_) => async.countDown()
      case Failure(result) => context.fail(result.getCause)
    }
    async.awaitSuccess()
  }

  private def createDummyChangeBookingRequest(): ChangeBookingRequest = {
    val request = new ChangeBookingRequest
    request.setUsername(Username)
    request.setNewLicensePlate(NewLicensePlate)
    request
  }

  @Test def completeBooking(context: TestContext): Unit = {
    val async = context.async(2)
    val queue = VehicleServiceSimulatedQueue + "CompleteBooking"
    val eventBusAddress = VehicleServiceSimulatedEventBusAddress + "CompleteBooking"
    eventBus.consumer(eventBusAddress, (message: Message[Object]) => message.body() match {
      case body: JsonObject =>
        val response: CompleteBookingResponse = VertxJsonMapper.mapFromBodyTo(body, classOf[CompleteBookingResponse])
        context.assertTrue(response != null && response.isSuccess)
        store.deleteAllBookings()
        async.complete()
      case _ => context.fail(); async.complete()
    })
    val config = ConsumerConfig(
      queue,
      durableQueue = false,
      Shared.RabbitMQ.Exchanges.BOOKING,
      Shared.RabbitMQ.RoutingKey.COMPLETE_BOOKING_RESPONSE,
      eventBusAddress
    )
    RabbitMQConsumers.createConsumer(client, config).flatMap(_ => {
      client.basicPublishFuture(
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.COMPLETE_BOOKING_REQUEST,
        VertxJsonMapper.mapInBodyFrom(createDummyCompleteBookingRequest()))
    }).onComplete {
      case Success(_) => async.countDown()
      case Failure(result) => context.fail(result.getCause)
    }
    async.awaitSuccess()
  }

  private def createDummyCompleteBookingRequest(): CompleteBookingRequest = {
    val request = new CompleteBookingRequest
    request.setUsername(Username)
    request.setLicensePlate(NewLicensePlate)
    request
  }

  @Test def findBookingPositions(context: TestContext): Unit = {
    val async = context.async(2)
    val queue = VehicleServiceSimulatedQueue + "FindBookingPositions"
    val eventBusAddress = VehicleServiceSimulatedEventBusAddress + "FindBookingPositions"
    eventBus.consumer(eventBusAddress, (message: Message[Object]) => message.body() match {
      case body: JsonObject =>
        val response: FindBookingPositionsResponse =
          VertxJsonMapper.mapFromBodyTo(body, classOf[FindBookingPositionsResponse])
        context.assertTrue(response != null && (!response.isSuccess || response.getUserPosition.equals(UserPosition)))
        async.complete()
      case _ => context.fail(); async.complete()
    })
    val config = ConsumerConfig(
      queue,
      durableQueue = false,
      Shared.RabbitMQ.Exchanges.BOOKING,
      Shared.RabbitMQ.RoutingKey.FIND_BOOKING_POSITION_RESPONSE,
      eventBusAddress
    )
    RabbitMQConsumers.createConsumer(client, config).flatMap(_ => {
      client.basicPublishFuture(
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.FIND_BOOKING_POSITION_REQUEST,
        VertxJsonMapper.mapInBodyFrom(createDummyFindBookingPositionsRequest()))
    }).onComplete {
      case Success(_) => async.countDown()
      case Failure(result) => context.fail(result.getCause)
    }
    async.awaitSuccess()
  }

  private def createDummyFindBookingPositionsRequest(): FindBookingPositionsRequest = {
    val request = new FindBookingPositionsRequest
    request.setUsername(Username)
    request
  }

}
