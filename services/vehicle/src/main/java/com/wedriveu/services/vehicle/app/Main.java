package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.Vertx;

public class Main {

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new BootVerticle(), deployed -> {
            vertx.eventBus().send(Messages.VehicleService.BOOT, null);
        });

    }

}
