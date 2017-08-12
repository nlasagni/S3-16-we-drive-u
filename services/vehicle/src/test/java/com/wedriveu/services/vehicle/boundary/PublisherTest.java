package com.wedriveu.services.vehicle.boundary;

import com.wedriveu.services.shared.rabbitmq.client.RabbitMQFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.services.shared.utilities.Constants.BODY;

/**
 * Created by Marco on 09/08/2017.
 */
public abstract class PublisherTest {

    private static final int DELAY = 10000;
    private RabbitMQClient rabbitMQClient;
    private String queue;
    private String exchangeName;
    private String requestRoutingKey;
    private String responseRoutingKey;
    private String eventBusAddress;
    private Vertx vertx;

    public PublisherTest(String queue,
                         String exchangeName,
                         String requestRoutingKey,
                         String responseRoutingKey,
                         String eventBusAddress) {
        this.queue = queue;
        this.exchangeName = exchangeName;
        this.requestRoutingKey = requestRoutingKey;
        this.responseRoutingKey = responseRoutingKey;
        this.eventBusAddress = eventBusAddress;
    }

    protected void setup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
        this.vertx = vertx;
        rabbitMQClient = RabbitMQFactory.createClient(vertx);
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
        if (!keyName.isEmpty()) {
            responseRoutingKey = String.format(responseRoutingKey, keyName);
        }
        rabbitMQClient.queueBind(queue, exchangeName, responseRoutingKey,
                onBind -> {
                    if (onBind.succeeded()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(io.vertx.core.Future.failedFuture(onBind.cause().getMessage()));
                    }
                });
    }

    protected void publishMessage(TestContext context, JsonObject data) {
        final Async async = context.async();
        context.assertNotNull(data);
        handleServiceResponse(context, async, eventBusAddress);
        rabbitMQClient.basicConsume(queue, eventBusAddress, context.asyncAssertSuccess(onGet -> {
            rabbitMQClient.basicPublish(exchangeName, requestRoutingKey, data,
                    context.asyncAssertSuccess(onPublish -> {
                        vertx.setTimer(DELAY, handler -> {
                            context.fail();
                            async.complete();
                        });
                    }));
        }));
    }

    private void handleServiceResponse(TestContext context, Async async, String eventBusAddress) {
        vertx.eventBus().consumer(eventBusAddress, msg -> {
            context.assertNotNull(msg.body());
            JsonObject responseJson = new JsonObject(((JsonObject) msg.body()).getString(BODY));
            checkResponse(responseJson);
            async.complete();
        }).exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });

    }

    protected abstract void checkResponse(JsonObject responseJson);

    protected abstract JsonObject getJson();
}
