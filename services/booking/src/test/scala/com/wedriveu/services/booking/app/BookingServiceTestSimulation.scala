package com.wedriveu.services.booking.app

import com.wedriveu.services.shared.vertx.VertxJsonMapper
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse
import com.wedriveu.shared.util.{Constants => Shared}
import io.vertx.scala.core.eventbus.{EventBus, Message}
import io.vertx.scala.rabbitmq.RabbitMQClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author Nicola Lasagni on 19/08/2017.
  */
object BookingServiceTestSimulation {

  def simulateVehicleService(
    client: RabbitMQClient,
    eventBus: EventBus,
    queue: String,
    eventBusAddress: String,
    response: BookVehicleResponse
  ): Future[_] = {
    eventBus.consumer(eventBusAddress, (msg: Message[Object]) => {
      client.basicPublishFuture(
        Shared.RabbitMQ.Exchanges.VEHICLE,
        Shared.RabbitMQ.RoutingKey.VEHICLE_SERVICE_BOOK_RESPONSE,
        VertxJsonMapper.mapInBodyFrom(response)
      )
    })
    client.exchangeDeclareFuture(
      Shared.RabbitMQ.Exchanges.VEHICLE,
      Shared.RabbitMQ.Exchanges.Type.DIRECT,
      durable = true,
      autoDelete = false
    ).flatMap(_ => {
      client.queueDeclareFuture(
        queue,
        durable = false,
        exclusive = false,
        autoDelete = true
      )
    }).flatMap(_ => {
      client.queueBindFuture(
        queue,
        Shared.RabbitMQ.Exchanges.VEHICLE,
        Shared.RabbitMQ.RoutingKey.VEHICLE_SERVICE_BOOK_REQUEST
      )
    }).flatMap(_ => {
      client.basicConsumeFuture(queue, eventBusAddress)
    })
  }

}
