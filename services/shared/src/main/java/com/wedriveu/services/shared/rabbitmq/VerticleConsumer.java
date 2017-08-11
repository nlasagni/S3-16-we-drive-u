package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Basic Vert.x RabbitMQ Consumer Verticle. Used to properly handle inbound messages from external publishers.
 *
 * @author nicolalasagni
 * @author Marco Baldassarri
 * @since 29/07/2017
 */
public abstract class VerticleConsumer extends AbstractVerticle {

    protected EventBus eventBus;
    private String queueName;
    private io.vertx.rabbitmq.RabbitMQClient client;
    private String name;

    public VerticleConsumer(String name) {
        this.name = name;
    }

    @Override
    public void start() throws Exception {
        Log.info("VERTICLE CONSUMER", "START SETUP");
        this.queueName = Constants.SERVICE_QUEUE_BASE_NAME + "." + name;
        eventBus = vertx.eventBus();
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
        Log.info("VERTICLE CONSUMER", "END SETUP");
    }

    protected void startConsumer(String exchange, String routingKey, String eventBusAddress)
            throws IOException, TimeoutException {

        startConsumer(onStart -> {
            Log.info("VERTICLE CONSUMER", "START CONSUMER");
            declareQueue(onQueue -> {
                Log.info("VERTICLE CONSUMER", "DECLARE QUEUE");
                if (onQueue.succeeded()) {
                    bindQueueToExchange(exchange,
                            routingKey, onBind -> {
                                Log.info("VERTICLE CONSUMER", "BIND QUEUE TO EXCHANGE");
                                if (onBind.succeeded()) {
                                    Log.info("VERTICLE CONSUMER", "BIND SUCCEEDED");
                                    registerConsumer(eventBusAddress);
                                    basicConsume(eventBusAddress);
                                } else {
                                    Log.info("VERTICLE CONSUMER", "BIND FAILED");
                                }
                            });
                }
            });
        });
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
        baseRoutingKey = name.isEmpty() ? String.format(baseRoutingKey, name) : baseRoutingKey;
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
        client.basicConsume(queueName, eventBus, null);
    }

}
