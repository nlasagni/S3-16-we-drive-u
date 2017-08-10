package com.wedriveu.services.vehicle.boundary;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rabbitmq.RabbitMQClient;
import org.junit.runner.RunWith;

/**
 * Created by Marco on 09/08/2017.
 */

public abstract class PublisherTest {

    private static final String JSON_QUEUE_KEY = "queue";
    private static final String PASSWORD = "password";
    private static final String HOST = "host";
    private static final long RESPONSE_TIME = 2000;
    private static final String TAG = PublisherTest.class.getSimpleName();
    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private String requestId;

    protected void setup(TestContext context, Verticle verticle) {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        JsonObject config = new JsonObject();
        config.put(HOST, Constants.RABBITMQ_SERVER_HOST);
        config.put(PASSWORD, Constants.RABBITMQ_SERVER_PASSWORD);
        Async async = context.async(3);
        rabbitMQClient = RabbitMQClient.create(vertx, config);
        rabbitMQClient.start(onStart -> {
            rabbitMQClient.queueDeclareAuto(onQueueDeclare -> {
                requestId = onQueueDeclare.result().getString(JSON_QUEUE_KEY);
                context.assertTrue(onQueueDeclare.succeeded());
                vertx.deployVerticle(verticle, context.asyncAssertSuccess(onDeploy ->
                        async.complete()
                ));
                async.countDown();
            });
            async.countDown();
        });
        async.awaitSuccess();
    }

    protected void stop(TestContext context) {
        rabbitMQClient.stop(context.asyncAssertSuccess());
    }

    protected void registerConsumer(TestContext context, String exchange, String routingKey, String eventBusAddress, JsonObject data) {
        Async async = context.async(3);
        rabbitMQClient.basicPublish(exchange, routingKey, data,
                onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(TAG, onPublish.cause().getLocalizedMessage(), onPublish.cause());
                    }
                    async.countDown();
                    vertx.setTimer(RESPONSE_TIME, onTimeOut -> {
                        eventBus.consumer(eventBusAddress, msg -> {
                            Log.log("Message received");
                            JsonObject responseJson = new JsonObject(msg.body().toString());
                            //LoginResponse response = objectMapper.readValue(responseJson.getString(Constants.EventBusMessage.BODY), LoginResponse.class);
                            context.assertTrue(!responseJson.isEmpty());
                            Log.log(responseJson.encodePrettily());
                            async.complete();

                        });
                        rabbitMQClient.basicConsume(requestId, eventBusAddress, onGet -> {
                        });
                    });
                });
        async.countDown();
        async.awaitSuccess();
    }

    protected abstract JsonObject getJson();
}
