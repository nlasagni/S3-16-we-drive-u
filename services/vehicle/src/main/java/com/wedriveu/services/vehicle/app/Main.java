package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.vehicle.boundary.nearest.NearestConsumerVerticle;
import com.wedriveu.services.vehicle.control.VerticleDeployer;
import io.vertx.core.Vertx;

public class Main {

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new VerticleDeployer(), completed -> {
            if (completed.succeeded()) {
                vertx.deployVerticle(new NearestConsumerVerticle());
            }
        });
    }

}
