package com.wedriveu.services.shared.rabbitmq;

import io.vertx.core.AbstractVerticle;<<<<<<< HEAD
import com.wedriveu.shared.util.Constants;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.shared.utilities.Constants.EXCHANGE_TYPE;

/**
 * Basic Vert.x RabbitMQ Consumer Verticle. Used to properly handle inbound messages from external publishers.
 *
 * @author Marco Baldassarri
 * @author Stefano Bernagozzi
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
        this.queueName = Constants.SERVICE_QUEUE_BASE_NAME + "." + name;
        eventBus = vertx.eventBus();
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
        Log.log("verticle consumer " + name + " rabbitmq client created " + client);
    }

    protected void startConsumerWithFuture(String exchange, String routingKey, String eventBusAddress, Future future)
            throws IOException, TimeoutException {
        startConsumer(onStart -> {
            if(!onStart.succeeded()) {
                Log.error("startConsumerWithFuture onstart", onStart.cause().getLocalizedMessage(),onStart.cause());
            }
            else {
                Log.info("startConsumerWithFuture","start succeded");
            }
            declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    bindQueueToExchange(exchange,
                            routingKey, onBind -> {
                                if (onBind.succeeded()) {
                                    registerConsumer(eventBusAddress);
                                    basicConsume(eventBusAddress);
                                    if (future != null) {
                                        Log.log("Completing future bind VehicleListRetrieverVerticle");
                                        future.complete();
                                    }
                                } else {
                                    Log.error("startConsumerWithFuture bind", onBind.cause().getLocalizedMessage(),onBind.cause());
                                }
                            });
                } else {
                    Log.error("startConsumerWithFuture queue", onQueue.cause().getLocalizedMessage(),onQueue.cause());
                }
            });
        });
    }

    protected void startConsumer(String exchange, String routingKey, String eventBusAddress)
            throws IOException, TimeoutException {
        startConsumerWithFuture(exchange, routingKey, eventBusAddress, null);
    }

    private void startConsumer(Handler<AsyncResult<Void>> handler) throws java.io.IOException, TimeoutException {
        System.out.println("startConsumer "+name+ " started "+(client == null));
        client.start(onStartCompleted -> {
            if (onStartCompleted.succeeded()) {
                Log.log("onStartCompleted.succeeded()");
                handler.handle(Future.succeededFuture());
            } else {
                Log.log(" onStartCompleted failed future");
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

    protected void declareExchangesWithName(String name, Handler<AsyncResult<Void>> handler) {
        client.exchangeDeclare(name,
                EXCHANGE_TYPE,
                false,
                false,
                handler);
    }


    public abstract void registerConsumer(String eventBus);

    private void basicConsume(String eventBus) {
        client.basicConsume(queueName, eventBus, null);
    }

}
