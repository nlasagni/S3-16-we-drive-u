package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.vehicle.boundary.nearest.VehicleElectionVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.RegisterConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.RegisterPublisherVerticle;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

/**
 * Deploys the whole Verticle collection once. This has been created in order to avoid deploying the same Verticles
 * multiple times at each user vehicle request.
 *
 * @author Marco Baldassarri
 * @since 04/08/2017
 */
public class VerticleDeployer extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        List<Future> futures = new ArrayList<>();
        Future controlFuture = Future.future();
        vertx.deployVerticle(new NearestControl(), controlFuture.completer());
        futures.add(controlFuture);

        Future electionFuture = Future.future();
        vertx.deployVerticle(new VehicleElectionVerticle(), electionFuture.completer());
        futures.add(electionFuture);

        Future storeFuture = Future.future();
        vertx.deployVerticle(new VehicleStoreImpl(), storeFuture.completer());
        futures.add(storeFuture);

        Future registerConsumerFuture = Future.future();
        vertx.deployVerticle(new RegisterConsumerVerticle(), registerConsumerFuture.completer());
        futures.add(registerConsumerFuture);

        Future registerPublisherFuture = Future.future();
        vertx.deployVerticle(new RegisterPublisherVerticle(), registerPublisherFuture.completer());
        futures.add(registerPublisherFuture);

        CompositeFuture.all(futures).setHandler(completed -> {
            if (completed.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(completed.cause());
            }
        });
    }

}

