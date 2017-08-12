package com.wedriveu.services.analytics.vehicleServiceFake;

import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleServiceFakeVerticleDeployer extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {

        List<Future> futures = new ArrayList<>();

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleListGeneratorRequestHandler(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);

        Future generatorResponseHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleListGeneratorResponseHandler(), generatorResponseHandlerFuture.completer());
        futures.add(generatorResponseHandlerFuture);

        CompositeFuture.all(futures).setHandler(completed -> {
            if (completed.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(completed.cause());
            }
        });
    }

}
