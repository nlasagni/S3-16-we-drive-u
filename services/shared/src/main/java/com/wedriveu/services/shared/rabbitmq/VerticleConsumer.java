package com.wedriveu.services.shared.rabbitmq;

import io.vertx.core.AbstractVerticle;
import com.wedriveu.shared.util.Constants;
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
 * @author Marco Baldassarri
 * @author Stefano Bernagozzi
 * @since 29/07/2017
 */
public abstract class VerticleConsumer extends AbstractVerticle {

    protected EventBus eventBus;
    private String queueName;
    private io.vertx.rabbitmq.RabbitMQClient client;
    private String name;

    protected void setQueueName(String queueName) {
        this.queueName = Constants.SERVICE_QUEUE_BASE_NAME + "." + queueName;
    }

    public VerticleConsumer(String name) {
        this.name = name;
    }

    @Override
    public void start() throws Exception {
        //this.queueName = Constants.SERVICE_QUEUE_BASE_NAME + "." + name;
        eventBus = vertx.eventBus();
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
        Log.log("verticle consumer " + name + " rabbitmq client created " + client);
    }

    protected void startConsumerWithFuture(String exchange, String routingKey, String eventBusAddress, Future future)
            throws IOException, TimeoutException {
        startConsumer(onStart -> {
            if(!onStart.succeeded()) {
                Log.error("name: " + name + " startConsumerWithFuture onstart", onStart.cause().getLocalizedMessage(),onStart.cause());
            }
            else {
                Log.info("name: " + name + " startConsumerWithFuture","start succeded");
            }
            declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    bindQueueToExchange(exchange,
                            routingKey, onBind -> {
                                if (onBind.succeeded()) {
                                    registerConsumer(eventBusAddress);
                                    basicConsume(eventBusAddress);
                                    if (future != null) {
                                        Log.log("name: " + name + " Completing future bind VehicleListRetrieverVerticle");
                                        future.complete();
                                    }
                                } else {
                                    Log.error("name: " + name + " startConsumerWithFuture bind", onBind.cause().getLocalizedMessage(),onBind.cause());
                                }
                            });
                } else {
                    Log.error("name: " + name + " startConsumerWithFuture queue", onQueue.cause().getLocalizedMessage(),onQueue.cause());
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
        Log.log(name + " declareQueue Queue name " + queueName);
        client.queueDeclare(queueName, true, false, false, onDeclareCompleted -> {
            if (onDeclareCompleted.succeeded()) {
                handler.handle(Future.succeededFuture());
            } else {
                Log.error(name + " declareQueue ", onDeclareCompleted.cause().getLocalizedMessage(), onDeclareCompleted.cause());
                handler.handle(Future.failedFuture(onDeclareCompleted.cause().getMessage()));
            }
        });
    }

    private void bindQueueToExchange(String exchangeName,
                                     String baseRoutingKey,
                                     Handler<AsyncResult<Void>> handler) {
        Log.log("Base routing key: " + baseRoutingKey);
        Log.log("exchangeName: " + exchangeName);
        Log.log("queueName: " + queueName);
        //baseRoutingKey = name.isEmpty() ? String.format(baseRoutingKey, name) : baseRoutingKey;
        declareExchangesWithName(exchangeName, onDeclare -> {
            client.queueBind(queueName,
                    exchangeName,
                    baseRoutingKey,
                    onBind -> {
                        if (onBind.succeeded()) {
                            handler.handle(Future.succeededFuture());
                        } else {
                            Log.error(name + " bindQueueToExchange", onBind.cause().getLocalizedMessage(), onBind.cause());
                            handler.handle(io.vertx.core.Future.failedFuture(onBind.cause().getMessage()));
                        }
                    });
        });
    }

    protected void declareExchangesWithName(String exchangeName, Handler<AsyncResult<Void>> handler) {
        client.exchangeDeclare(exchangeName,
                Constants.RabbitMQ.Exchanges.Type.DIRECT,
                false,
                false,
                handler);
    }


    public abstract void registerConsumer(String eventBus);

    private void basicConsume(String eventBus) {
        client.basicConsume(queueName, eventBus, null);
    }

}
