package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.boundary.nearest.NearestConsumerVerticle;
import com.wedriveu.services.vehicle.control.VerticleDeployer;
import io.vertx.core.Vertx;

import static com.wedriveu.services.shared.utilities.Constants.VEHICLE_SERVICE_EXCHANGE;

public class Main {

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        VerticlePublisher.startAndDeclareExchange(vertx, VEHICLE_SERVICE_EXCHANGE);
        vertx.deployVerticle(new VerticleDeployer(), completed -> {
            if (completed.succeeded()) {
                vertx.deployVerticle(new NearestConsumerVerticle());
            }
        });
    }

}
