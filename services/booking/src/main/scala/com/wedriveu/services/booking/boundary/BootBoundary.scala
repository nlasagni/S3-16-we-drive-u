package com.wedriveu.services.booking.boundary

import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory
import com.wedriveu.shared.util.{Constants => Shared}
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.rabbitmq.RabbitMQClient

import scala.concurrent.Future

/** This trait is used to represent an actor that boot
  * all the boundary components.
  *
  * @author Nicola Lasagni on 17/08/2017.
  *
  */
trait BootBoundary {

  /** Boots all the boundary components.
    *
    * @return The future of all the boot operations
    */
  def boot(): Future[_]

}

object BootBoundary {

  /**
    * The [[ScalaVerticle]] name that executes the boot operations
    */
  val Verticle: String = s"scala:${classOf[BootBoundaryImpl].getName}"

  private class BootBoundaryImpl() extends ScalaVerticle with BootBoundary {

    private[this] var client: RabbitMQClient = _

    override def startFuture(): Future[_] = {
      client = RabbitMQClient.create(vertx, RabbitMQClientFactory.createClientConfig())
      boot()
    }

    override def boot(): Future[_] = {
      client.startFuture().flatMap({
        _ => client.exchangeDeclareFuture(
          Shared.RabbitMQ.Exchanges.BOOKING,
          Shared.RabbitMQ.Exchanges.Type.DIRECT,
          durable = true,
          autoDelete = false
        )
      }).flatMap({
        _ => vertx.deployVerticleFuture(BookingBoundary.Verticle)
      }).flatMap({
        _ => client.stopFuture()
      })
    }

  }

}
