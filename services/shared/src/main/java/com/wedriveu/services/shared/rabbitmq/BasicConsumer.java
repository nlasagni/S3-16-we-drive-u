package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.concurrent.TimeoutException;

/**
 * Created by nicolalasagni on 29/07/2017.
 */
public abstract class BasicConsumer {

    protected String tag;
    protected String queueName;
    protected Vertx vertx;
    protected io.vertx.rabbitmq.RabbitMQClient client;
    protected String name;
    protected EventBus eventBus;

    public BasicConsumer(String name) {
        this.name = name;
        this.tag = name;
        this.queueName = Constants.QUEUE_BASE_NAME + "-" + name;
        this.vertx = Vertx.vertx();
        eventBus = vertx.eventBus();

        client = RabbitMQClientConfig.getInstance().getRabbitMQClient();
    }

    public void start(Handler<AsyncResult<Void>> handler) throws java.io.IOException, TimeoutException {
        client.start(onStartCompleted -> {
            if (onStartCompleted.succeeded()) {
                Log.info(tag, "RabbitMQ client started");
                handler.handle(Future.succeededFuture());
            } else {
                Log.error(tag, onStartCompleted.cause().getMessage(), onStartCompleted.cause());
                handler.handle(Future.failedFuture(onStartCompleted.cause().getMessage()));
            }
        });
    }

    public void declareQueue(Handler<AsyncResult<Void>> handler) {
        // Here we could also use only one queue instead of creating a new queue for
        // every user
        client.queueDeclare(queueName, true, false, false, onDeclareCompleted -> {
            if (onDeclareCompleted.succeeded()) {
                String queueName = onDeclareCompleted.result().getString(Constants.QUEUE_NAME_JSON_KEY);
                Log.info(tag, "Declared queue " + queueName);
                handler.handle(Future.succeededFuture());
            } else {
                Log.error(tag, onDeclareCompleted.cause().getMessage(), onDeclareCompleted.cause());
                handler.handle(Future.failedFuture(onDeclareCompleted.cause().getMessage()));
            }
        });
    }

    public void bindQueueToExchange(String exchangeName,
                                    String baseRoutingKey,
                                    Handler<AsyncResult<Void>> handler) {
        // es vehicle.nearest.%s
        baseRoutingKey =  name.isEmpty() ? String.format(baseRoutingKey, name) : baseRoutingKey;
        client.queueBind(queueName,
                exchangeName,
                baseRoutingKey,
                onBind -> {
                    if (onBind.succeeded()) {
                        Log.info(tag,
                                "Bound " + queueName + " to exchange \"" + exchangeName + "\"");
                        handler.handle(Future.succeededFuture());
                    } else {
                        Log.error(tag, onBind.cause().getMessage(), onBind.cause());
                        handler.handle(io.vertx.core.Future.failedFuture(onBind.cause().getMessage()));
                    }
                });
    }

    public abstract void registerConsumer(String eventBus);

    public void basicConsume(String eventBus) {
        client.basicConsume(queueName, eventBus, onRegistered -> {
            if (onRegistered.succeeded()) {
                Log.info(tag,"Registered to queue " + queueName);
            } else {
                Log.error(tag, onRegistered.cause().getMessage(), onRegistered.cause());
            }
        });
    }


}
