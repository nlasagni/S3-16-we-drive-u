package com.wedriveu.services.authentication.boundary;

import com.wedriveu.services.authentication.util.Constants;
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.LoginRequest;
import com.wedriveu.shared.rabbitmq.message.LoginResponse;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * @author Stefano Bernagozzi on 11/07/2017.
 * @author Nicola Lasagni on 04/08/2017
 *         <p>
 *         This class represents the Boundary of the Authentication micro-service.
 *         It allows users to login into the WeDriveU system through its API.
 */
public class AuthenticationBoundaryVerticle extends AbstractVerticle implements AuthenticationBoundary {

    private static final String TAG = AuthenticationBoundaryVerticle.class.getSimpleName();
    private static final String QUEUE_NAME = "service.authentication";
    private static final String EVENT_BUS_ADDRESS = "service.authentication.login";
    private static final String SEND_ERROR = "Error occurred while sending response.";

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        eventBus = vertx.eventBus();
        eventBus.consumer(Constants.EventBus.LOGIN_COMPLETED, this::sendResponse);
        startService(startFuture);
    }

    @Override
    public void login(LoginRequest loginRequest) {
        eventBus.send(Constants.EventBus.START_LOGIN, VertxJsonMapper.mapInBodyFrom(loginRequest));
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            LoginRequest request = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), LoginRequest.class);
            login(request);
        });
    }

    private void sendResponse(Message<JsonObject> message) {
        String requestId = message.body().getString(Constants.Message.REQUEST_ID);
        JsonObject responseString = new JsonObject(message.body().getString(Constants.Message.RESPONSE));
        LoginResponse response = VertxJsonMapper.mapTo(responseString, LoginResponse.class);
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.NO_EXCHANGE,
                requestId,
                VertxJsonMapper.mapInBodyFrom(response),
                onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(TAG, SEND_ERROR, onPublish.cause());
                    }
                });
    }

    private void startService(Future<Void> future) {
        rabbitMQClient = RabbitMQClientFactory.createClient(vertx);
        Future<Void> initFuture = Future.future();
        Future<Void> endFuture = Future.future();
        startClient(initFuture);
        initFuture.compose(v -> {
            Future<JsonObject> declareQueueFuture = Future.future();
            declareQueue(declareQueueFuture);
            return declareQueueFuture;
        }).compose(v -> {
            Future<Void> exchangeDeclareFuture = Future.future();
            exchangeDeclare(exchangeDeclareFuture);
            return exchangeDeclareFuture;
        }).compose(v -> {
            Future<Void> bindQueueFuture = Future.future();
            bindQueueToExchange(bindQueueFuture);
            return bindQueueFuture;
        }).compose(v -> {
            Future<Void> basicConsumeFuture = Future.future();
            basicConsume(basicConsumeFuture);
            return basicConsumeFuture;
        }).compose(v -> {
            registerConsumer();
            future.complete();
        }, endFuture);
    }

    private void startClient(Future<Void> future) {
        rabbitMQClient.start(future.completer());
    }

    private void declareQueue(Future<JsonObject> future) {
        rabbitMQClient.queueDeclare(QUEUE_NAME,
                true,
                false,
                false,
                future.completer());
    }

    private void exchangeDeclare(Future<Void> future) {
        rabbitMQClient.exchangeDelete(Constants.RabbitMQ.Exchanges.USER, onDelete -> {
            rabbitMQClient.exchangeDeclare(Constants.RabbitMQ.Exchanges.USER,
                    Constants.RabbitMQ.Exchanges.Type.DIRECT,
                    true,
                    false,
                    future.completer());
        });
    }

    private void bindQueueToExchange(Future<Void> future) {
        rabbitMQClient.queueBind(QUEUE_NAME,
                Constants.RabbitMQ.Exchanges.USER,
                Constants.RabbitMQ.RoutingKey.LOGIN,
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(QUEUE_NAME, EVENT_BUS_ADDRESS, future.completer());
    }

}
