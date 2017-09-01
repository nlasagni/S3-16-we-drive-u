package com.wedriveu.services.shared.rabbitmq;


import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Basic Vert.x RabbitMQ Consumer Verticle. Used to properly handle inbound messages from external publishers.
 *
 * @author Marco Baldassarri
 */
public abstract class VerticleConsumer extends AbstractVerticle {

    protected EventBus eventBus;
    private String queueName;
    protected RabbitMQClient client;

    public VerticleConsumer(String name) {
        this.queueName = name;
    }

    @Override
    public void start() throws Exception {
        eventBus = vertx.eventBus();
        client = RabbitMQClientFactory.createClient(vertx);
    }

    protected void startConsumerWithFuture(String exchange,
                                           String routingKey,
                                           String eventBusAddress,
                                           Future future)
            throws IOException, TimeoutException {
        startConsumer(onStart -> {
            declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    bindQueueToExchange(exchange,
                            routingKey,
                            onBind -> {
                                if (onBind.succeeded()) {
                                    registerConsumer(eventBusAddress);
                                    basicConsume(eventBusAddress, future == null ? null : future.completer());
                                } else {
                                    Log.error(queueName + " bind ", onBind.cause().getLocalizedMessage(), onBind.cause());
                                }
                            });
                } else {
                    Log.error(queueName, onQueue.cause().getLocalizedMessage(), onQueue.cause());
                }
            });
        });
    }

    private void startConsumer(Handler<AsyncResult<Void>> handler) throws java.io.IOException, TimeoutException {
        client.start(handler);
    }

    private void declareQueue(Handler<AsyncResult<JsonObject>> handler) {
        client.queueDeclare(queueName, true, false, false, handler);
    }

    private void bindQueueToExchange(String exchangeName,
                                     String baseRoutingKey,
                                     Handler<AsyncResult<Void>> handler) {
        client.queueBind(queueName, exchangeName, baseRoutingKey, handler);
    }

    public abstract void registerConsumer(String eventBus);

    private void basicConsume(String eventBus, Handler<AsyncResult<Void>> handler) {
        client.basicConsume(queueName, eventBus, handler);
    }

}
