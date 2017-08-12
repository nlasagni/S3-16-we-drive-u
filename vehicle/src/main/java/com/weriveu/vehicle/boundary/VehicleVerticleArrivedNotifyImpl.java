package com.weriveu.vehicle.boundary;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.shared.entity.ArrivedNotify;
import com.wedriveu.shared.utils.Log;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.shared.EventBusConstants$;
import com.wedriveu.vehicle.shared.Exchanges$;
import com.wedriveu.vehicle.shared.RoutingKeys$;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
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
    private Exchanges$ exchanges = Exchanges$.MODULE$;
    private RoutingKeys$ routingKeys = RoutingKeys$.MODULE$;
    private EventBusConstants$ eventBusConstants = EventBusConstants$.MODULE$;

    public VehicleVerticleArrivedNotifyImpl(VehicleControl vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
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
            future.complete();
        }, endFuture);
    }

    private void startClient(Future<Void> future) {
        rabbitMQClient.start(future.completer());
    }

    @Override
    public void sendArrivedNotify(ArrivedNotify notify) {
        rabbitMQClient.basicPublish(exchanges.VEHICLE(), routingKeys.VEHICLE_ARRIVED(), createNotify(notify), onPublish -> {
            onPublish.succeeded();
            if(onPublish.failed()){
                Log.error(TAG, SEND_ERROR);
            }
        });
    }

    private JsonObject createNotify(ArrivedNotify notify) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(eventBusConstants.BODY(), JsonObject.mapFrom(notify).toString());
        return jsonObject;
    }

}
