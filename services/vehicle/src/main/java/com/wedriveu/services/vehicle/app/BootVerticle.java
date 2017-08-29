package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.vehicle.boundary.nearest.NearestConsumerVerticle;
import com.wedriveu.services.vehicle.control.VerticleDeployer;
import com.wedriveu.services.vehicle.rabbitmq.ExchangeManagerVerticle;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

/**
 * Basic Verticle used to declare the VehicleService exchange used for RabbitMQ communications.
 * Once the exchange has being set, all the application Verticles are being deployed in this class as well
 *
 * @author Marco Baldassarri on 11/08/2017.
 */
public class BootVerticle extends AbstractVerticle {

    private ExchangeManagerVerticle exchangeVerticle = new ExchangeManagerVerticle();

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(Messages.VehicleService.BOOT, this::declareExchange);
        vertx.eventBus().consumer(Messages.VehicleService.EXCHANGE_DECLARED, this::deployVerticles);
    }

    private void declareExchange(Message message) {
        vertx.deployVerticle(exchangeVerticle, completed -> {
            vertx.eventBus().send(Messages.VehicleService.DECLARE_EXCHANGE,
                    Constants.RabbitMQ.Exchanges.VEHICLE);
        });
    }

    private void deployVerticles(Message message) {
        vertx.deployVerticle(new VerticleDeployer(), deployerCompleted -> {
            if (deployerCompleted.succeeded()) {
                vertx.deployVerticle(new NearestConsumerVerticle(), onNearestCompleted -> {
                    vertx.undeploy(exchangeVerticle.deploymentID());
                    vertx.eventBus().send(Messages.VehicleService.BOOT_COMPLETED, null);
                });
            }
        });
    }

}
