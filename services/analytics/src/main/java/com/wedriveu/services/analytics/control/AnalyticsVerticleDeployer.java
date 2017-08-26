package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.boundary.*;
import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.ExchangeManagerVerticleWithQueue;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;

import java.util.ArrayList;
import java.util.List;

import static com.wedriveu.services.analytics.util.EventBus.Messages.ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE;

/**
 * a verticle that deploys all the other verticles of the analytics service
 *
 * @author Stefano Bernagozzi
 */
public class AnalyticsVerticleDeployer extends AbstractVerticle {
    Future startFuture;
    ExchangeManagerVerticleWithQueue exchangeManagerVerticleWithQueue = new ExchangeManagerVerticleWithQueue(
            EventBus.EVENTBUS_RECEIVE_VERTICLE_DEPLOYER,
            EventBus.EVENTBUS_SEND_VERTICLE_DEPLOYER
    );
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        this.startFuture = startFuture;
        vertx.eventBus().consumer(EventBus.EVENTBUS_SEND_VERTICLE_DEPLOYER, this::deployVerticles);

        vertx.deployVerticle(exchangeManagerVerticleWithQueue, res-> {
            vertx.eventBus().send(EventBus.EVENTBUS_RECEIVE_VERTICLE_DEPLOYER, Constants.RabbitMQ.Exchanges.ANALYTICS);

        });

    }

    public void deployVerticles(Message message) {
        List<Future> futures = new ArrayList<>();
        Future requestFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleListRequestPublisher(), requestFuture.completer());
        futures.add(requestFuture);

        Future retrieverFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleListRetrieverConsumer(), retrieverFuture.completer());
        futures.add(retrieverFuture);

        Future controlFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleDataManipulationVerticle(), controlFuture.completer());
        futures.add(controlFuture);

        Future vehicleUpdate = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleUpdateHandlerConsumer(), vehicleUpdate.completer());
        futures.add(vehicleUpdate);

        Future counterRequest = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleRequestConsumer(), counterRequest.completer());
        futures.add(counterRequest);

        Future counterSend = Future.future();
        vertx.deployVerticle(new AnalyticsVehiclesResponsePublisher(), counterSend.completer());
        futures.add(counterSend);


        CompositeFuture.all(futures).setHandler(completed -> {
            if (completed.succeeded()) {
                vertx.eventBus().send(EventBus.VEHICLE_LIST_REQUEST, ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE);
                vertx.undeploy(exchangeManagerVerticleWithQueue.deploymentID());
                startFuture.complete();
            } else {
                startFuture.fail(completed.cause());
            }
        });
    }
}
