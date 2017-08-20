package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.vehicle.boundary.analytics.AnalyticsConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.analytics.AnalyticsPublisherVerticle;
import com.wedriveu.services.vehicle.boundary.booking.BookConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.booking.BookPublisherVerticle;
import com.wedriveu.services.vehicle.boundary.booking.StartDrivingPublisherVerticle;
import com.wedriveu.services.vehicle.boundary.nearest.VehicleElectionVerticle;
import com.wedriveu.services.vehicle.boundary.updates.UpdatesVerticle;
import com.wedriveu.services.vehicle.boundary.vehiclearrived.VehicleArrivedVerticle;
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
 * @author Marco Baldassarri on 04/08/2017.
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

        Future analyticsConsumerFuture = Future.future();
        vertx.deployVerticle(new AnalyticsConsumerVerticle(), analyticsConsumerFuture.completer());
        futures.add(analyticsConsumerFuture);

        Future analyticsPublisherFuture = Future.future();
        vertx.deployVerticle(new AnalyticsPublisherVerticle(), analyticsPublisherFuture.completer());
        futures.add(analyticsPublisherFuture);

        Future bookingControlFuture = Future.future();
        vertx.deployVerticle(new BookingControl(), bookingControlFuture.completer());
        futures.add(bookingControlFuture);

        Future bookConsumerVerticleFuture = Future.future();
        vertx.deployVerticle(new BookConsumerVerticle(), bookConsumerVerticleFuture.completer());
        futures.add(bookConsumerVerticleFuture);

        Future bookPublisherVerticleFuture = Future.future();
        vertx.deployVerticle(new BookPublisherVerticle(), bookPublisherVerticleFuture.completer());
        futures.add(bookPublisherVerticleFuture);

        Future startDrivingPublisherFuture = Future.future();
        vertx.deployVerticle(new StartDrivingPublisherVerticle(), startDrivingPublisherFuture.completer());
        futures.add(startDrivingPublisherFuture);

        Future arrivedVerticleFuture = Future.future();
        vertx.deployVerticle(new VehicleArrivedVerticle(), arrivedVerticleFuture.completer());
        futures.add(arrivedVerticleFuture);

        Future updateVerticleFuture = Future.future();
        vertx.deployVerticle(new UpdatesVerticle(), updateVerticleFuture.completer());
        futures.add(updateVerticleFuture);

        CompositeFuture.all(futures).setHandler(completed -> {
            if (completed.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(completed.cause());
            }
        });
    }

}

