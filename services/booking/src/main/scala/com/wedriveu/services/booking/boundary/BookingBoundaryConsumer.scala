package com.wedriveu.services.booking.boundary

import com.wedriveu.services.booking.util.Constants
import com.wedriveu.shared.util.{Constants => Shared}
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory
import io.vertx.core.json.JsonObject
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.eventbus.Message
import io.vertx.scala.rabbitmq.RabbitMQClient

import scala.concurrent.Future

/** Trait that represents a booking messages consumer.
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
  * @param queue The queue name
  * @param durableQueue Indicates if a queue is durable or not
  * @param exchange The exchange name
  * @param routingKey The routing key name
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

  private[this] abstract class BookingBoundaryConsumerVerticle extends ScalaVerticle with BookingBoundaryConsumer {

    private[this] var client: RabbitMQClient = _
    private[this] var consumerConfig: BookingVerticleConsumerConfig = _
    private[this] val consumerAddress: String = this.getClass.getCanonicalName

    override def startFuture(): Future[_] = {
      consumerConfig = buildConfig
      client = RabbitMQClient.create(vertx, RabbitMQClientFactory.createClientConfig())
      consume()
      client.startFuture().flatMap(_ => declareQueue()).flatMap(_ => bindQueue()).flatMap(_ => basicConsume())
    }

    private def declareQueue(): Future[JsonObject] = {
      client.queueDeclareFuture(
        consumerConfig.queue,
        consumerConfig.durableQueue,
        exclusive = false,
        autoDelete = false
      )
    }

    private def bindQueue(): Future[Unit] = {
      client.queueBindFuture(consumerConfig.queue, consumerConfig.exchange, consumerConfig.routingKey)
    }

    private def basicConsume(): Future[Unit] = {
      client.basicConsumeFuture(consumerConfig.queue, consumerAddress)
    }

    override def consume(): Unit = {
      vertx.eventBus().consumer(consumerAddress, (message: Message[Object]) => {
        vertx.eventBus().send(consumerConfig.eventBusAddress, message.body())
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
        Shared.RabbitMQ.RoutingKey.BOOK_VEHICLE_RESPONSE,
        Constants.EventBus.Address.Vehicle.BookVehicleResponse)
    }
  }


}


