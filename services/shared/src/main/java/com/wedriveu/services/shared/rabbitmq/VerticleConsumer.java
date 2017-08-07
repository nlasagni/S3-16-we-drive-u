package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;

import java.util.concurrent.TimeoutException;

/**
 * Created by nicolalasagni on 29/07/2017.
 */
public abstract class VerticleConsumer extends AbstractVerticle {

    private static final String RABBIT_MQ_CLIENT_STARTED = "RabbitMQ client started";
    private static final String DECLARED_QUEUE = "Declared queue ";
    private static final String BOUND = "Bound ";
    private static final String TO_EXCHANGE = " to exchange ";
    private static final String REGISTERED_TO_QUEUE = "Registered to queue ";
    protected EventBus eventBus;
    private String tag;
    private String queueName;
    private io.vertx.rabbitmq.RabbitMQClient client;
    private String name;

    public VerticleConsumer(String name) {
        this.name = name;
    }

    @Override
    public void start() throws Exception {
        this.tag = name;
        this.queueName = Constants.SERVICE_QUEUE_BASE_NAME + "." + name;
        eventBus = vertx.eventBus();
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
    }

    protected void startConsumer(Handler<AsyncResult<Void>> handler) throws java.io.IOException, TimeoutException {
        client.start(onStartCompleted -> {
            if (onStartCompleted.succeeded()) {
                Log.info(tag, RABBIT_MQ_CLIENT_STARTED);
                handler.handle(Future.succeededFuture());
            } else {
                Log.error(tag, onStartCompleted.cause().getMessage(), onStartCompleted.cause());
                handler.handle(Future.failedFuture(onStartCompleted.cause().getMessage()));
            }
        });
    }

    protected void declareQueue(Handler<AsyncResult<Void>> handler) {
        client.queueDeclare(queueName, true, false, false, onDeclareCompleted -> {
            if (onDeclareCompleted.succeeded()) {
                String queueName = onDeclareCompleted.result().getString(Constants.QUEUE_NAME_JSON_KEY);
                Log.info(tag, DECLARED_QUEUE + queueName);
                handler.handle(Future.succeededFuture());
            } else {
                Log.error(tag, onDeclareCompleted.cause().getMessage(), onDeclareCompleted.cause());
                handler.handle(Future.failedFuture(onDeclareCompleted.cause().getMessage()));
            }
        });
    }

    protected void bindQueueToExchange(String exchangeName,
                                       String baseRoutingKey,
                                       Handler<AsyncResult<Void>> handler) {
        // i.e. vehicle.nearest.%s
        baseRoutingKey = name.isEmpty() ? String.format(baseRoutingKey, name) : baseRoutingKey;
        client.queueBind(queueName,
                exchangeName,
                baseRoutingKey,
                onBind -> {
                    if (onBind.succeeded()) {
                        Log.info(tag, BOUND + queueName + TO_EXCHANGE + exchangeName);
                        handler.handle(Future.succeededFuture());
                    } else {
                        Log.error(tag, onBind.cause().getMessage(), onBind.cause());
                        handler.handle(io.vertx.core.Future.failedFuture(onBind.cause().getMessage()));
                    }
                });
    }

    public abstract void registerConsumer(String eventBus);

    protected void basicConsume(String eventBus) {
        client.basicConsume(queueName, eventBus, onRegistered -> {
            if (onRegistered.succeeded()) {
                Log.info(tag, REGISTERED_TO_QUEUE + queueName);
            } else {
                Log.error(tag, onRegistered.cause().getMessage(), onRegistered.cause());
            }
        });
    }

}
