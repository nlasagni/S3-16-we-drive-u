package com.wedriveu.services.vehicle.boundary;

import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;

/**
 * Basic RabbitMQ communication client (publisher and consumer) for testing VehicleService
 * inbound and outbound communication.
 *
 * @author Marco on 09/08/2017.
 */
public abstract class BaseInteractionClient {

    private static final int TIME_OUT = 12000;
    private RabbitMQClient rabbitMQClient;
    private String queue;
    private String consumerExchangeName;
    private String consumerRoutingKey;
    private String eventBusAddress;
    private Vertx vertx;

    public BaseInteractionClient(String queue,
                                 String consumerExchangeName,
                                 String consumerRoutingKey,
                                 String eventBusAddress) {
        this.queue = queue;
        this.consumerExchangeName = consumerExchangeName;
        this.consumerRoutingKey = consumerRoutingKey;
        this.eventBusAddress = eventBusAddress;
    }

    protected void setup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
        this.vertx = vertx;
        rabbitMQClient = RabbitMQClientFactory.createClient(vertx);
        rabbitMQClient.start(handler);

    }

    private void declareQueue(Handler<AsyncResult<Void>> handler) {
        rabbitMQClient.queueDeclare(queue, true, false, false, onDeclareCompleted -> {
            if (onDeclareCompleted.succeeded()) {
                handler.handle(Future.succeededFuture());
            } else {
                handler.handle(Future.failedFuture(onDeclareCompleted.cause().getMessage()));
            }
        });
    }

    protected void stop(TestContext context) {
        rabbitMQClient.stop(context.asyncAssertSuccess());
    }

    protected void declareQueueAndBind(String keyName, TestContext context, Handler<AsyncResult<Void>> handler) {
        declareQueue(onDeclareCompleted -> {
            context.assertTrue(onDeclareCompleted.succeeded());
            bindQueueToExchange(keyName, handler);
        });
    }

    private void bindQueueToExchange(String keyName, Handler<AsyncResult<Void>> handler) {
        if (keyName != null && !keyName.isEmpty()) {
            consumerRoutingKey = String.format(consumerRoutingKey, keyName);
        }
        rabbitMQClient.queueBind(queue, consumerExchangeName, consumerRoutingKey,
                onBind -> {
                    if (onBind.succeeded()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(io.vertx.core.Future.failedFuture(onBind.cause().getMessage()));
                    }
                });
    }

    protected void publishMessage(TestContext context,
                                  boolean withTimeOut,
                                  String publishExchange,
                                  String publishRoutingKey,
                                  JsonObject data) {
        final Async async = context.async();
        context.assertNotNull(data);
        handleServiceResponse(context, async, eventBusAddress);
        rabbitMQClient.basicConsume(queue, eventBusAddress, context.asyncAssertSuccess(onGet -> {
            rabbitMQClient.basicPublish(publishExchange, publishRoutingKey, data,
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(this.getClass().getSimpleName(), onPublish.cause());
                        }
                        if (withTimeOut) {
                            vertx.setTimer(TIME_OUT, handler -> {
                                context.fail();
                                async.complete();
                            });
                        }
                    });
        }));
    }

    private void handleServiceResponse(TestContext context, Async async, String eventBusAddress) {
        vertx.eventBus().consumer(eventBusAddress, msg -> {
            context.assertNotNull(msg.body());
            JsonObject responseJson = new JsonObject(((JsonObject) msg.body()).getString(BODY));
            checkResponse(context, responseJson);
            async.complete();
        }).exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });

    }

    protected abstract void checkResponse(TestContext context, JsonObject responseJson);

}
