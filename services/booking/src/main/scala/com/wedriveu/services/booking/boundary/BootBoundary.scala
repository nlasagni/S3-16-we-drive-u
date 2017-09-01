package com.wedriveu.services.booking.boundary

import com.wedriveu.services.booking.util.BootManager
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory
import com.wedriveu.shared.util.{Constants => Shared}
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.rabbitmq.RabbitMQClient

import scala.concurrent.Future

/** Creates a [[BootManager]] that boots the [[BookingBoundary]]
  *
  */
object BootBoundary {

  /**
    * The [[ScalaVerticle]] name that executes the boot operations
    */
  val Verticle: String = s"scala:${classOf[BootBoundaryImpl].getName}"

  private class BootBoundaryImpl() extends ScalaVerticle with BootManager {

    private[this] var client: RabbitMQClient = _

    override def startFuture(): Future[_] = {
      client = RabbitMQClient.create(vertx, RabbitMQClientFactory.createClientConfig())
      boot()
    }

    override def boot(): Future[_] = {
      client.startFuture().flatMap({
        _ =>
          client.exchangeDeclareFuture(
            Shared.RabbitMQ.Exchanges.BOOKING,
            Shared.RabbitMQ.Exchanges.Type.DIRECT,
            durable = true,
            autoDelete = false
          )
      }).flatMap({
        _ => vertx.deployVerticleFuture(BookingBoundaryVerticle.Verticle)
      }).flatMap({
        _ => client.stopFuture()
      })
    }

  }

}
