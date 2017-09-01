package com.wedriveu.vehicle.boundary.mock;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;

/**
 * @author Nicola Lasagni on 22/08/2017.
 */
public class UserEnterVehicleMockVerticle extends VerticleConsumer {

    private static final String EVENT_BUS_ADDRESS =
            UserEnterVehicleMockVerticle.class.getCanonicalName() + ".eventBus";
    private static final String QUEUE =
            UserEnterVehicleMockVerticle.class.getCanonicalName() + ".queue";

    private String username;
    private String vehicleLicensePlate;

    public UserEnterVehicleMockVerticle(String username, String vehicleLicensePlate) {
        super(QUEUE);
        this.username = username;
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST_ENTER_USER, username),
                EVENT_BUS_ADDRESS,
                startFuture);
    }

    @Override
    public void registerConsumer(String eventBusAddress) {
        eventBus.consumer(eventBusAddress, msg -> {
            client.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                    String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE_ENTER_USER, vehicleLicensePlate),
                    VertxJsonMapper.mapInBodyFrom(new EnterVehicleResponse()),
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(this.getClass().getSimpleName(), onPublish.cause());
                        }
                    });
        });
    }
}
