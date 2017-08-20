package com.wedriveu.services.vehicle.boundary.updates;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.services.vehicle.rabbitmq.SubstitutionRequest;
import com.wedriveu.services.vehicle.rabbitmq.UserRequest;
import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingRequest;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.util.Date;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.NEAREST_EVENT_BUS_ADDRESS;

/**
 * Created by Michele on 19/08/2017.
 */

public class UpdatesVerticle extends AbstractVerticle {

    private static final String TAG = UpdatesVerticle.class.getSimpleName();
    private static final String QUEUE_NAME = "service.updates";
    private static final String EVENT_BUS_ADDRESS = "service.updates.eventbus";
    private static final String SUBSTITUTION_BUS_ADDRESS = "service.substitution.eventbus";

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
                Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE,
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(QUEUE_NAME, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            UpdateToService update = VertxJsonMapper.mapFromBodyTo((JsonObject)msg.body(), UpdateToService.class);
            if(update.getStatus().equals(Vehicle.STATUS_BROKEN_STOLEN)) {
                Position vehiclePosition = update.getPosition();
                SubstitutionRequest request = new SubstitutionRequest();
                request.setPosition(vehiclePosition);
                vertx.eventBus().send(SUBSTITUTION_BUS_ADDRESS, VertxJsonMapper.mapInBodyFrom(request));
            }
            eventBus.send(Messages.VehicleStore.UPDATE_VEHICLE_STATUS, VertxJsonMapper.mapInBodyFrom(update));
        });
    }

}
