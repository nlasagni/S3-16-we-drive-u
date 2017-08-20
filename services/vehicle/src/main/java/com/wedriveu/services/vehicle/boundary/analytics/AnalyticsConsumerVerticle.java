package com.wedriveu.services.vehicle.boundary.analytics;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_ANALYTICS_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_REGISTER;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLE_REQUEST_ALL;


/**
 * Vert.x RabbitMQ Consumer that listens for Analytics Service requests. It asks for the whole list of Vehicles
 * currently saved in the database file.
 *
 * @author Marco Baldassarri on 15/08/2017.
 */
public class AnalyticsConsumerVerticle extends VerticleConsumer {

    public AnalyticsConsumerVerticle() {
        super(VEHICLE_SERVICE_QUEUE_REGISTER);
    }

    @Override
    public void start() throws Exception {
        super.start();
        startVehicleRegisterConsumer();
    }

    private void startVehicleRegisterConsumer() throws IOException, TimeoutException {
        startConsumer(false, Constants.RabbitMQ.Exchanges.VEHICLE,
                ANALYTICS_VEHICLE_REQUEST_ALL,
                EVENT_BUS_ANALYTICS_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            getVehicles();
        });
    }

    private void getVehicles() {
        eventBus.send(Messages.Analytics.GET_VEHICLES_REQUEST, null);
    }

}
