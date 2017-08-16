package com.wedriveu.services.shared.rabbitmq;


import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Basic Vert.x RabbitMQ Consumer Verticle. Used to properly handle inbound messages from external publishers.
 *
 * @author Marco Baldassarri
 * @since 29/07/2017
 */
public abstract class VerticleConsumer extends AbstractVerticle {

    protected EventBus eventBus;
    private String queueName;
    private RabbitMQClient client;

    public VerticleConsumer(String name) {
        this.queueName = name;
    }

    @Override
    public void start() throws Exception {
        eventBus = vertx.eventBus();
        client = RabbitMQClientFactory.createClient(vertx);
    }

    protected void startConsumerWithDurableQueue(String exchange, String routingKey, String eventBusAddress)
            throws IOException, TimeoutException {
        startConsumer(exchange, routingKey, eventBusAddress);
    }

    protected void startConsumerWithFuture(String exchange, String routingKey, String eventBusAddress, Future future)
            throws IOException, TimeoutException {
        startConsumer(onStart -> {
            declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    bindQueueToExchange(exchange,
                            routingKey, onBind -> {
                                if (onBind.succeeded()) {
                                    registerConsumer(eventBusAddress);
                                    basicConsume(eventBusAddress);
                                    if (future != null) {
                                        future.complete();
                                    }
                                }
                            });
                }
            });
        });
    }

    protected void startConsumer(String exchange, String routingKey, String eventBusAddress)
            throws IOException, TimeoutException {
        startConsumerWithFuture(exchange, routingKey, eventBusAddress, null);
    }

    private void startConsumer(Handler<AsyncResult<Void>> handler) throws java.io.IOException, TimeoutException {
        client.start(onStartCompleted -> {
            if (onStartCompleted.succeeded()) {
                handler.handle(Future.succeededFuture());
            } else {
                handler.handle(Future.failedFuture(onStartCompleted.cause().getMessage()));
            }
        });
    }

    private void declareQueue(Handler<AsyncResult<Void>> handler) {
        client.queueDeclare(queueName, true, false, false, onDeclareCompleted -> {
            if (onDeclareCompleted.succeeded()) {
                handler.handle(Future.succeededFuture());
            } else {
                handler.handle(Future.failedFuture(onDeclareCompleted.cause().getMessage()));
            }
        });
    }

    private void bindQueueToExchange(String exchangeName,
                                     String baseRoutingKey,
                                     Handler<AsyncResult<Void>> handler) {
        client.queueBind(queueName,
                exchangeName,
                baseRoutingKey,
                onBind -> {
                    if (onBind.succeeded()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(io.vertx.core.Future.failedFuture(onBind.cause().getMessage()));
                    }
                });
    }

    public abstract void registerConsumer(String eventBus);

    private void basicConsume(String eventBus) {
        client.basicConsume(queueName, eventBus, handler -> { });
    }

}
