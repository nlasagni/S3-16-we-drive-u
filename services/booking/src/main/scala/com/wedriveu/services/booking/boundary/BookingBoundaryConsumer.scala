package com.wedriveu.services.booking.boundary

import com.wedriveu.services.booking.util.{Constants, ConsumerConfig, RabbitMQConsumers}
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory
import com.wedriveu.shared.util.{Constants => Shared}
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.eventbus.Message
import io.vertx.scala.rabbitmq.RabbitMQClient

import scala.concurrent.Future

/** Trait that represents a booking messages consumer.
  *
  * @author Nicola Lasagni on 16/08/2017.
  */
trait BookingBoundaryConsumer {

  /**
    * Consume a message.
    */
  def consume(): Unit

  /** Gets the [[BookingVerticleConsumerConfig]].
    *
    * @return The [[BookingVerticleConsumerConfig]] needed to consume messages.
    */
  def buildConfig: BookingVerticleConsumerConfig

}

/** Configuration needed to start a [[BookingBoundaryConsumer]]
  *
  * @param queue           The queue name
  * @param durableQueue    Indicates if a queue is durable or not
  * @param exchange        The exchange name
  * @param routingKey      The routing key name
  * @param eventBusAddress The destination event bus address name
  */
private[boundary] case class BookingVerticleConsumerConfig(
  queue: String,
  durableQueue: Boolean = true,
  exchange: String,
  routingKey: String,
  eventBusAddress: String
) {
  private val invalidConfig: String = "Consumer configuration not provided"
  require(checkQueue() && checkExchange() && checkRoutingKey() && checkEventBusAddress(), invalidConfig)

  private def checkQueue() = queue != null && !queue.isEmpty

  private def checkExchange() = exchange != null && !exchange.isEmpty

  private def checkRoutingKey() = routingKey != null && !routingKey.isEmpty

  private def checkEventBusAddress() = eventBusAddress != null && !eventBusAddress.isEmpty

}

object BookingBoundaryConsumerVerticle {

  /**
    * The [[ScalaVerticle]] name to deploy a [[CreateBookingBoundaryConsumer]]
    */
  val CreateBooking: String = s"scala:${classOf[CreateBookingBoundaryConsumer].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[ChangeBookingBoundaryConsumer]]
    */
  val ChangeBooking: String = s"scala:${classOf[ChangeBookingBoundaryConsumer].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[CompleteBookingBoundaryConsumer]]
    */
  val CompleteBooking: String = s"scala:${classOf[CompleteBookingBoundaryConsumer].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[FindBookingPositionBoundaryConsumer]]
    */
  val FindBookingPosition: String = s"scala:${classOf[FindBookingPositionBoundaryConsumer].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[BookVehicleBoundaryConsumer]]
    */
  val BookVehicle: String = s"scala:${classOf[BookVehicleBoundaryConsumer].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[GetBookingsConsumer]]
    */
  val GetBookings: String = s"scala:${classOf[GetBookingsConsumer].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[AbortBookingConsumer]]
    */
  val AbortBooking: String = s"scala:${classOf[AbortBookingConsumer].getName}"

  private[this] abstract class BookingBoundaryConsumerVerticle extends ScalaVerticle with BookingBoundaryConsumer {

    private[this] var client: RabbitMQClient = _
    private[this] var bookingConsumerConfig: BookingVerticleConsumerConfig = _
    private[this] val consumerAddress: String = this.getClass.getCanonicalName

    override def startFuture(): Future[_] = {
      bookingConsumerConfig = buildConfig
      client = RabbitMQClient.create(vertx, RabbitMQClientFactory.createClientConfig())
      consume()
      client.startFuture().flatMap(_ => {
        RabbitMQConsumers.createConsumer(
          client,
          ConsumerConfig(
            bookingConsumerConfig.queue,
            bookingConsumerConfig.durableQueue,
            bookingConsumerConfig.exchange,
            bookingConsumerConfig.routingKey,
            consumerAddress)
        )
      })
    }

    override def consume(): Unit = {
      vertx.eventBus().consumer(consumerAddress, (message: Message[Object]) => {
        vertx.eventBus().send(bookingConsumerConfig.eventBusAddress, message.body())
      })
    }

  }

  private[this] class CreateBookingBoundaryConsumer extends BookingBoundaryConsumerVerticle {
    override def buildConfig: BookingVerticleConsumerConfig = {
      BookingVerticleConsumerConfig(Constants.Queue.Create,
        durableQueue = true,
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.CREATE_BOOKING_REQUEST,
        Constants.EventBus.Address.Booking.CreateBookingRequest)
    }
  }

  private[this] class ChangeBookingBoundaryConsumer extends BookingBoundaryConsumerVerticle {
    override def buildConfig: BookingVerticleConsumerConfig = {
      BookingVerticleConsumerConfig(Constants.Queue.Change,
        durableQueue = true,
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.CHANGE_BOOKING_REQUEST,
        Constants.EventBus.Address.Booking.ChangeBookingLicensePlateRequest)
    }
  }

  private[this] class CompleteBookingBoundaryConsumer extends BookingBoundaryConsumerVerticle {
    override def buildConfig: BookingVerticleConsumerConfig = {
      BookingVerticleConsumerConfig(Constants.Queue.Complete,
        durableQueue = true,
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.COMPLETE_BOOKING_REQUEST,
        Constants.EventBus.Address.Booking.CompleteBookingRequest)
    }
  }

  private[this] class FindBookingPositionBoundaryConsumer extends BookingBoundaryConsumerVerticle {
    override def buildConfig: BookingVerticleConsumerConfig = {
      BookingVerticleConsumerConfig(Constants.Queue.FindPosition,
        durableQueue = true,
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.FIND_BOOKING_POSITION_REQUEST,
        Constants.EventBus.Address.Booking.FindBookingPositionRequest)
    }
  }

  private[this] class BookVehicleBoundaryConsumer extends BookingBoundaryConsumerVerticle {
    override def buildConfig: BookingVerticleConsumerConfig = {
      BookingVerticleConsumerConfig(Constants.Queue.BookVehicle,
        durableQueue = true,
        Shared.RabbitMQ.Exchanges.VEHICLE,
        Shared.RabbitMQ.RoutingKey.VEHICLE_SERVICE_BOOK_RESPONSE,
        Constants.EventBus.Address.Vehicle.BookVehicleResponse)
    }
  }

  private[this] class GetBookingsConsumer extends BookingBoundaryConsumerVerticle {
    override def buildConfig: BookingVerticleConsumerConfig = {
      BookingVerticleConsumerConfig(Constants.Queue.GetBookings,
        durableQueue = true,
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.BOOKING_REQUEST_BOOKING_LIST,
        Constants.EventBus.Address.Booking.GetBookingsRequest)
    }
  }

  private[this] class AbortBookingConsumer extends BookingBoundaryConsumerVerticle {
    override def buildConfig: BookingVerticleConsumerConfig = {
      BookingVerticleConsumerConfig(Constants.Queue.AbortBookings,
        durableQueue = true,
        Shared.RabbitMQ.Exchanges.BOOKING,
        Shared.RabbitMQ.RoutingKey.ABORT_BOOKING_REQUEST,
        Constants.EventBus.Address.Booking.AbortBookingRequest)
    }
  }


}


