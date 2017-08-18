package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.boundary.AnalyticsVehicleListRequestPublisher;
import com.wedriveu.services.analytics.boundary.AnalyticsVehicleListRetrieverConsumer;
import com.wedriveu.services.analytics.boundary.*;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVerticleDeployer extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {

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
                Log.log("starting event message queue");
                vertx.eventBus().send(ANALYTICS_VEHICLE_LIST_REQUEST_EVENTBUS, ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE);
                startFuture.complete();
            } else {
                Log.error("failing starting some verticle", completed.cause().getLocalizedMessage(), completed.cause());
                startFuture.fail(completed.cause());
            }
        });
    }

}
