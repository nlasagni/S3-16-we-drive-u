package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.vehicle.boundary.analytics.AnalyticsConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.analytics.AnalyticsPublisherVerticle;
import com.wedriveu.services.vehicle.boundary.booking.*;
import com.wedriveu.services.vehicle.boundary.nearest.VehicleElectionVerticle;
import com.wedriveu.services.vehicle.boundary.updates.UpdatesVerticle;
import com.wedriveu.services.vehicle.boundary.booking.CompleteBookingVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.RegisterConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.RegisterPublisherVerticle;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.shared.util.Log;
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
 * @author Nicola Lasagni
 */
public class VerticleDeployer extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        List<Future> futures = new ArrayList<>();
        AbstractVerticle[] verticles = {
                new NearestControl(),
                new VehicleElectionVerticle(),
                new VehicleStoreImpl(),
                new RegisterConsumerVerticle(),
                new RegisterPublisherVerticle(),
                new AnalyticsConsumerVerticle(),
                new AnalyticsPublisherVerticle(),
                new BookingControl(),
                new BookConsumerVerticle(),
                new BookPublisherVerticle(),
                new AbortBookingPublisherVerticle(),
                new StartDrivingPublisherVerticle(),
                new CompleteBookingVerticle(),
                new UpdatesVerticle(),
                new UpdateControl(),
                new SubstitutionControl(),
                new ChangeBookingConsumerVerticle(),
                new ChangeBookingPublisherVerticle(),
                new GetBookingPositionsVerticle()
        };
        for (AbstractVerticle verticle : verticles) {
            Future<String> future = Future.future();
            vertx.deployVerticle(verticle, future.completer());
            futures.add(future);
        }
        CompositeFuture.all(futures).setHandler(completed -> {
            if (completed.succeeded()) {
                startFuture.complete();
            } else {
                Log.error(this.getClass().getSimpleName(), completed.cause());
                startFuture.fail(completed.cause());
            }
        });
    }

}

