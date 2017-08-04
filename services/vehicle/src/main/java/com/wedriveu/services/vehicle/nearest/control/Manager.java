package com.wedriveu.services.vehicle.nearest.control;

import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.vehicle.nearest.boundary.election.VehicleElection;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

/**
 * Deploys the whole verticle collection once. This has been created in order to avoid deploying the same verticles
 * multiple times at each user vehicle request.
 * Created by Marco on 04/08/2017.
 */
public class Manager extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        List<Future> futures = new ArrayList<>();

        Future controlFuture = Future.future();
        vertx.deployVerticle(new ControlImpl(), controlFuture.completer());
        futures.add(controlFuture);

        Future electionFuture = Future.future();
        vertx.deployVerticle(new VehicleElection(), electionFuture.completer());
        futures.add(electionFuture);

        Future storeFuture = Future.future();
        vertx.deployVerticle(new VehicleStoreImpl(), storeFuture.completer());
        futures.add(storeFuture);

        CompositeFuture.all(futures).setHandler(completed -> {
            if (completed.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(completed.cause());
            }
        });


    }

}

