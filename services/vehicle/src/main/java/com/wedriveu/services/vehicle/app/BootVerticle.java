package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.shared.rabbitmq.ExchangeManagerVerticle;
import com.wedriveu.services.shared.rabbitmq.SharedMessages;
import com.wedriveu.services.vehicle.boundary.nearest.NearestConsumerVerticle;
import com.wedriveu.services.vehicle.control.VerticleDeployer;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

import static com.wedriveu.services.shared.utilities.Constants.VEHICLE_SERVICE_EXCHANGE;

/**
 * Basic Verticle used to declare the VehicleService exchange used for RabbitMQ communications.
 * Once the exchange has being set, all the application Verticles are being deployed in this class as well
 *
 * @author Marco Baldassarri on 11/08/2017.
 */
public class BootVerticle extends AbstractVerticle {

    ExchangeManagerVerticle exchangeVerticle = new ExchangeManagerVerticle();

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(Messages.VehicleService.BOOT, this::declareExchange);
        vertx.eventBus().consumer(SharedMessages.VehicleService.EXCHANGE_BINDED, this::deployVerticles);
    }

    private void declareExchange(Message message) {
        vertx.deployVerticle(exchangeVerticle, completed -> {
            vertx.eventBus().send(SharedMessages.VehicleService.BIND_EXCHANGE, VEHICLE_SERVICE_EXCHANGE);
        });
    }

    private void deployVerticles(Message message) {
        vertx.deployVerticle(new VerticleDeployer(), deployerCompleted -> {
            if (deployerCompleted.succeeded()) {
                vertx.deployVerticle(new NearestConsumerVerticle());
            }
        });
        vertx.undeploy(exchangeVerticle.deploymentID());
    }

}
