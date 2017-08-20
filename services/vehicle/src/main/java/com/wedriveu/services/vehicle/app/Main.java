package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.Vertx;

public class Main {

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, completed -> {
        });
        vertx.deployVerticle(new BootVerticle(), deployed -> {
            vertx.eventBus().send(Messages.VehicleService.BOOT, null);
        });
    }

}
