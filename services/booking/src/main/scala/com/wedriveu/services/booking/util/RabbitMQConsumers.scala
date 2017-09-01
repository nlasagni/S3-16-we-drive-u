package com.wedriveu.services.booking.util

import io.vertx.core.json.JsonObject
import io.vertx.scala.rabbitmq.RabbitMQClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author Nicola Lasagni on 19/08/2017.
  */
/** Configuration needed to start a RabbitMQ consumer.
  *
  * @param queue           The queue name
  * @param durableQueue    Indicates if a queue is durable or not
  * @param exchange        The exchange name
  * @param routingKey      The routing key name
  * @param eventBusAddress The destination event bus address name
  */
case class ConsumerConfig(
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

/** Utility object for creating RabbitMQ consumers.
  *
  */
object RabbitMQConsumers {

  /** Creates a consumer.
    *
    * @param client The [[RabbitMQClient]] with which create the consumer
    * @param config The [[ConsumerConfig]] with which create the consumer
    * @return A [[Future]] of the result of this operation
    */
  def createConsumer(client: RabbitMQClient, config: ConsumerConfig): Future[_] = {
    client.startFuture().flatMap(_ =>
      declareQueue(client, config)
    ).flatMap(_ => bindQueue(client, config)
    ).flatMap(_ => basicConsume(client, config))
  }

  private def declareQueue(client: RabbitMQClient, config: ConsumerConfig): Future[JsonObject] = {
    client.queueDeclareFuture(
      config.queue,
      config.durableQueue,
      exclusive = false,
      autoDelete = false
    )
  }

  private def bindQueue(client: RabbitMQClient, config: ConsumerConfig): Future[Unit] = {
    client.queueBindFuture(config.queue, config.exchange, config.routingKey)
  }

  private def basicConsume(client: RabbitMQClient, config: ConsumerConfig): Future[Unit] = {
    client.basicConsumeFuture(config.queue, config.eventBusAddress)
  }

}
