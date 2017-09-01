package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.vehicle.control.VehicleControl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * @author Michele Donati on 11/08/2017.
 */

public class VehicleVerticleArrivedNotifyImpl extends AbstractVerticle implements VehicleVerticleArrivedNotify {

    private VehicleControl vehicle;
    private static final String TAG = VehicleVerticleArrivedNotifyImpl.class.getSimpleName();
    private static final String SEND_ERROR = "Error occurred while sending request.";

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;

    public VehicleVerticleArrivedNotifyImpl(VehicleControl vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        eventBus = vertx.eventBus();
        startService(startFuture);
    }

    private void startService(Future<Void> future) {
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
        Future<Void> initFuture = Future.future();
        Future<Void> endFuture = Future.future();
        startClient(initFuture);
        initFuture.compose(v -> {
            registerConsumer();
            future.complete();
        }, endFuture);
    }

    private void startClient(Future<Void> future) {
        rabbitMQClient.start(future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_NOTIFY, vehicle.getVehicle().plate()),
                message -> {
                    sendArrivedNotify();
                });
    }

    @Override
    public void sendArrivedNotify() {
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_ARRIVED,
                createNotify(),
                onPublish -> {
                    vehicle.setUsername(null);
                    onPublish.succeeded();
                    if (onPublish.failed()) {
                        Log.error(TAG, SEND_ERROR);
                    }
                });
    }

    private JsonObject createNotify() {
        ArrivedNotify notifyToSend = new ArrivedNotify();
        notifyToSend.setLicense(vehicle.getVehicle().plate());
        notifyToSend.setUsername(vehicle.getUsername());
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(notifyToSend).toString());
        return jsonObject;
    }

}
