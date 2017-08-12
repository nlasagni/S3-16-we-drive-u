package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.boundary.*;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

import static com.wedriveu.services.shared.utilities.Constants.ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE;
import static com.wedriveu.services.shared.utilities.Constants.ANALYTICS_VEHICLE_LIST_REQUEST_VERTICLE_ADDRESS;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVerticleDeployer extends AbstractVerticle {
    private int counter;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        List<Future> futures = new ArrayList<>();
        Future requestFuture = Future.future();
        vertx.deployVerticle(new VehicleListRequestVerticle(), requestFuture.completer());
        futures.add(requestFuture);

        Future retrieverFuture = Future.future();
        vertx.deployVerticle(new VehicleListRetrieverVerticle(), retrieverFuture.completer());
        futures.add(retrieverFuture);

        Future controlFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVerticleController(), controlFuture.completer());
        futures.add(controlFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleListGeneratorRequestHandler(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);

        Future generatorResponseHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleListGeneratorResponseHandler(), generatorResponseHandlerFuture.completer());
        futures.add(generatorResponseHandlerFuture);

        CompositeFuture.all(futures).setHandler(completed -> {
            if (completed.succeeded()) {
                Log.log("starting event message queue");
                vertx.eventBus().send(ANALYTICS_VEHICLE_LIST_REQUEST_VERTICLE_ADDRESS, ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE);
                startFuture.complete();
            } else {
                Log.error("failing starting some verticle", completed.cause().getLocalizedMessage(), completed.cause());
                startFuture.fail(completed.cause());
            }
        });
    }

}
