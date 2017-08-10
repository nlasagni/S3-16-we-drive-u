package com.wedriveu.services.vehicle.boundary;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.services.shared.utilities.Constants.BODY;
import static com.wedriveu.services.shared.utilities.Constants.REGISTER_RESULT;

/**
 * Created by Marco on 09/08/2017.
 */

public abstract class PublisherTest {


    private static final String PASSWORD = "password";
    private static final String HOST = "host";
    private static final long RESPONSE_TIME = 2000;
    private static final String TAG = PublisherTest.class.getSimpleName();

    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;

    private String queue;
    private String exchangeName;
    private String requestRoutingKey;
    private String responseRoutingKey;
    private String eventBusAddress;


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

    protected void setup(TestContext context, Verticle verticle) {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        JsonObject config = new JsonObject();
        config.put(HOST, Constants.RABBITMQ_SERVER_HOST);
        config.put(PASSWORD, Constants.RABBITMQ_SERVER_PASSWORD);
        Async async = context.async(1);
        rabbitMQClient = RabbitMQClient.create(vertx, config);
        rabbitMQClient.start(onStart -> {

            vertx.deployVerticle(verticle, context.asyncAssertSuccess(onDeploy ->
                    async.complete()
            ));
            async.countDown();
        });
        async.awaitSuccess();
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


    protected void declareQueueAndBind(String keyName, TestContext context) {
        Async async = context.async(1);
        declareQueue(onDeclareCompleted -> {
            context.assertTrue(onDeclareCompleted.succeeded());
            bindQueueToExchange(keyName, onQueueBinded-> {
                context.assertTrue(onQueueBinded.succeeded());

                async.countDown();
            });
        });
        async.awaitSuccess();
    }

    private void bindQueueToExchange(String keyName, Handler<AsyncResult<Void>> handler) {
        if(!keyName.isEmpty()) {
            responseRoutingKey = String.format(responseRoutingKey, keyName);
        }
        //responseRoutingKey = keyName.isEmpty() ? responseRoutingKey : String.format(responseRoutingKey, keyName);
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
        final Async async = context.async(2);
        Log.info("PUBLISH", "\nEXCHANGE_NAME:" + exchangeName + "\nROUTING_KEY: "+ requestRoutingKey +
        "\nDATA: " + data.encodePrettily());
        rabbitMQClient.basicPublish(exchangeName, requestRoutingKey, data,
                onPublish -> {
                    context.assertTrue(onPublish.succeeded());
                    checkServiceResponse(context, async, eventBusAddress);
                    async.countDown();
                });
        async.awaitSuccess();
    }

    private void checkServiceResponse(TestContext context, Async async, String eventBusAddress) {
        rabbitMQClient.basicConsume(queue, eventBusAddress, onGet -> {
            Log.info(TAG, "message received");
        });
        MessageConsumer<JsonObject> consumer = eventBus.consumer(eventBusAddress, msg -> {
            JsonObject responseJson = new JsonObject(msg.body().getString(BODY));
            Log.info("RESPONSE", responseJson.encodePrettily());
            boolean response = responseJson.getBoolean(REGISTER_RESULT);
            context.assertTrue(response);
            async.complete();
        });
        consumer.exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });
    }

    protected abstract JsonObject getJson();
}