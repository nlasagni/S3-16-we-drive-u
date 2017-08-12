package com.wedriveu.services.vehicle.boundary;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
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

    private static final String PASSWORD = "password";
    private static final String HOST = "host";
    private static final String TAG = PublisherTest.class.getSimpleName();

    private RabbitMQClient rabbitMQClient;
    private String queue;
    private String exchangeName;
    private String requestRoutingKey;
    private String responseRoutingKey;
    private String eventBusAddress;
    private Vertx vertx;
    private JsonObject config;

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
        config = new JsonObject();
        config.put(HOST, Constants.RABBITMQ_SERVER_HOST);
        config.put(PASSWORD, Constants.RABBITMQ_SERVER_PASSWORD);
        rabbitMQClient = RabbitMQClient.create(vertx, config);
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
                        Log.info("BIND_TO_EXCHANGE", "EXCHANGE_NAME: " + exchangeName + ", ROUTING_KEY: " + responseRoutingKey);
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(io.vertx.core.Future.failedFuture(onBind.cause().getMessage()));
                    }
                });
    }

    protected void publishMessage(TestContext context, JsonObject data) {
        final Async async = context.async();
        context.assertNotNull(data);
        Log.info("PUBLISH_TEST", "HANDLE RESPONSE");
        Log.info("PUBLISH_TEST", "QUEUE: " + queue + ", EVENT_BUS_ADDRESS: " + eventBusAddress);
        handleServiceResponse(context, async, eventBusAddress);
        rabbitMQClient.basicConsume(queue, eventBusAddress, context.asyncAssertSuccess(onGet -> {
            Log.info("PUBLISH_TEST", "BASIC CONSUME");
            rabbitMQClient.basicPublish(exchangeName, requestRoutingKey, data,
                    context.asyncAssertSuccess(onPublish -> {
                        Log.info("PUBLISH_TEST", "BASIC PUBLISH");
                        vertx.setTimer(10000, handler -> {
                            context.fail();
                            async.complete();
                        });
                    }));
        }));
        //async.awaitSuccess();
    }

    private void handleServiceResponse(TestContext context, Async async, String eventBusAddress) {
        vertx.eventBus().consumer(eventBusAddress, msg -> {
            context.assertNotNull(msg.body());
            JsonObject responseJson = new JsonObject(((JsonObject)msg.body()).getString(BODY));
            Log.info("*********************", "IT WORKS");
            checkResponse(responseJson);
            Log.info("CONSUME", "EXCHANGE_NAME:" + exchangeName +
                    ", ROUTING_KEY: " + responseRoutingKey +
                    ", DATA: " + responseJson.encodePrettily());
            async.complete();
        }).exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });

    }

    protected abstract void checkResponse(JsonObject responseJson);

    protected abstract JsonObject getJson();
}
