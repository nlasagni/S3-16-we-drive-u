package com.wedriveu.services.booking.boundary

import com.wedriveu.services.booking.util.Constants
import com.wedriveu.shared.util.{Log, Constants => Shared}
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.core.eventbus.Message
import io.vertx.scala.rabbitmq.RabbitMQClient

import scala.concurrent.Future

/**
  * @author Nicola Lasagni on 18/08/2017.
  */
trait BookingBoundaryPublisher {

  def publish(message: JsonObject): Unit

}

object BookingBoundaryPublisherVerticle {

  /**
    * The [[ScalaVerticle]] name to deploy a [[BookVehiclePublisher]]
    */
  val BookVehicle: String = s"scala:${classOf[BookVehiclePublisher].getName}"
  /**
    * The [[ScalaVerticle]] name to deploy a [[CreateBookingVehiclePublisher]]
    */
  val CreateBooking: String = s"scala:${classOf[CreateBookingVehiclePublisher].getName}"

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

  private[this] class CreateBookingVehiclePublisher extends BookingBoundaryPublisherVerticleImpl(
    Constants.EventBus.Address.Booking.CreateBookingResponse,
    Shared.RabbitMQ.Exchanges.BOOKING,
    Shared.RabbitMQ.RoutingKey.CREATE_BOOKING_RESPONSE
  )

}
