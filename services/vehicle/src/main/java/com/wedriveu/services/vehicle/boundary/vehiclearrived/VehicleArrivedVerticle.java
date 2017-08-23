package com.wedriveu.services.vehicle.boundary.vehiclearrived;

import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingRequest;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * Created by Michele on 19/08/2017.
 */

public class VehicleArrivedVerticle extends AbstractVerticle {

    private static final String TAG = VehicleArrivedVerticle.class.getSimpleName();
    private static final String QUEUE_NAME = "service.vehicle.arrived";
    private static final String EVENT_BUS_ADDRESS = "service.vehicle.arrived.eventbus";

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        eventBus = vertx.eventBus();
        startService(startFuture);
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

    private void bindQueueToExchange(Future<Void> future) {
        rabbitMQClient.queueBind(QUEUE_NAME,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_ARRIVED,
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(QUEUE_NAME, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            ArrivedNotify notify = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), ArrivedNotify.class);
            rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.BOOKING,
                    Constants.RabbitMQ.RoutingKey.COMPLETE_BOOKING_REQUEST,
                    createObject(notify),
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(TAG, onPublish.cause());
                        }
                    });
        });
    }

    private JsonObject createObject(ArrivedNotify notify) {
        CompleteBookingRequest request = new CompleteBookingRequest();
        request.setUsername(notify.getUsername());
        request.setLicensePlate(notify.getLicense());
        return VertxJsonMapper.mapInBodyFrom(request);
    }

}
