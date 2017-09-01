package com.wedriveu.services.booking.boundary

import com.wedriveu.services.booking.util.Constants
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory
import com.wedriveu.shared.util.{Log, Constants => Shared}
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.eventbus.Message
import io.vertx.scala.rabbitmq.RabbitMQClient

import scala.concurrent.Future

/** A boundary message publisher used by the [[BookingBoundary]].
  *
  * @author Nicola Lasagni on 18/08/2017.
  */
trait BookingBoundaryPublisher {

  /** Publishes a message
    *
    * @param message The message to be published
    */
  def publish(message: JsonObject): Unit

}

/** Creates all the [[BookingBoundaryPublisher]] needed.
  *
  */
object BookingBoundaryPublisherVerticle {

  /**
    * The [[ScalaVerticle]] name to deploy a [[BookVehiclePublisher]]
    */
  val BookVehicle: String = s"scala:${classOf[BookVehiclePublisher].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[CreateBookingPublisher]]
    */
  val CreateBooking: String = s"scala:${classOf[CreateBookingPublisher].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[ChangeBookingPublisher]]
    */
  val ChangeBooking: String = s"scala:${classOf[ChangeBookingPublisher].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[CompleteBookingVehicleServicePublisher]]
    */
  val CompleteBookingVehicleService: String = s"scala:${classOf[CompleteBookingVehicleServicePublisher].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[CompleteBookingUserPublisher]]
    */
  val CompleteBookingUser: String = s"scala:${classOf[CompleteBookingUserPublisher].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[FindBookingPositionPublisher]]
    */
  val FindBookingPosition: String = s"scala:${classOf[FindBookingPositionPublisher].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[GetBookingsPublisher]]
    */
  val GetBookings: String = s"scala:${classOf[GetBookingsPublisher].getName}"

  private[this] class BookingBoundaryPublisherVerticleImpl(
    private val address: String,
    private val exchange: String,
    private val baseRoutingKey: String,
    private var specificKey: Option[String] = None
  ) extends ScalaVerticle with BookingBoundaryPublisher {

    private[this] val InvalidMessageReceived = "Invalid message received"
    private[this] val UnableToPublish = "Unable to publish message %s to exchange %s, routingKey %s"
    private[this] var client: RabbitMQClient = _

    override def startFuture(): Future[_] = {
      client = RabbitMQClient.create(vertx, RabbitMQClientFactory.createClientConfig())
      vertx.eventBus().consumer(address, (msg: Message[Object]) => msg.body() match {
        case body: JsonObject =>
          specificKey = Option.apply(body.remove(Constants.EventBus.Message.SpecificRoutingKey).asInstanceOf[String])
          publish(body)
        case _ => throw new IllegalArgumentException(InvalidMessageReceived)
      })
      client.startFuture()
    }

    private def getRoutingKey = specificKey match {
      case Some(key) => String.format(baseRoutingKey, key)
      case None => baseRoutingKey
    }

    override def publish(message: JsonObject): Unit = {
      client.basicPublish(exchange, getRoutingKey, message, handler => {
        if (handler.failed()) {
          Log.error(
            this.getClass.getSimpleName,
            String.format(UnableToPublish, message.toString, exchange, getRoutingKey), handler.cause())
        }
      })
    }
  }

  private[this] class BookVehiclePublisher extends BookingBoundaryPublisherVerticleImpl(
    Constants.EventBus.Address.Vehicle.BookVehicleRequest,
    Shared.RabbitMQ.Exchanges.VEHICLE,
    Shared.RabbitMQ.RoutingKey.VEHICLE_SERVICE_BOOK_REQUEST
  )

  private[this] class CreateBookingPublisher extends BookingBoundaryPublisherVerticleImpl(
    Constants.EventBus.Address.Booking.CreateBookingResponse,
    Shared.RabbitMQ.Exchanges.BOOKING,
    Shared.RabbitMQ.RoutingKey.CREATE_BOOKING_RESPONSE
  )

  private[this] class ChangeBookingPublisher extends BookingBoundaryPublisherVerticleImpl(
    Constants.EventBus.Address.Booking.ChangeBookingLicensePlateResponse,
    Shared.RabbitMQ.Exchanges.BOOKING,
    Shared.RabbitMQ.RoutingKey.CHANGE_BOOKING_RESPONSE
  )

  private[this] class CompleteBookingVehicleServicePublisher extends BookingBoundaryPublisherVerticleImpl(
    Constants.EventBus.Address.Booking.CompleteBookingVehicleServiceResponse,
    Shared.RabbitMQ.Exchanges.BOOKING,
    Shared.RabbitMQ.RoutingKey.COMPLETE_BOOKING_RESPONSE
  )

  private[this] class CompleteBookingUserPublisher extends BookingBoundaryPublisherVerticleImpl(
    Constants.EventBus.Address.Booking.CompleteBookingUserResponse,
    Shared.RabbitMQ.Exchanges.BOOKING,
    Shared.RabbitMQ.RoutingKey.COMPLETE_BOOKING_RESPONSE_USER
  )

  private[this] class FindBookingPositionPublisher extends BookingBoundaryPublisherVerticleImpl(
    Constants.EventBus.Address.Booking.FindBookingPositionResponse,
    Shared.RabbitMQ.Exchanges.BOOKING,
    Shared.RabbitMQ.RoutingKey.FIND_BOOKING_POSITION_RESPONSE
  )

  private[this] class GetBookingsPublisher extends BookingBoundaryPublisherVerticleImpl(
    Constants.EventBus.Address.Booking.GetBookingsResponse,
    Shared.RabbitMQ.Exchanges.BOOKING,
    Shared.RabbitMQ.RoutingKey.BOOKING_RESPONSE_BOOKING_LIST
  )

}
