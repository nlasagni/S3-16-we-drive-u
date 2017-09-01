package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.analytics.vehicleService.VehicleUpdateGenerator;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Stefano Bernagozzi
 */
@RunWith(VertxUnitRunner.class)
public class AnalyticsVehicleUpdateHandlerConsumerTest {

    private List<Future> futures;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new VehicleUpdateGenerator(), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future updaterFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleUpdateHandlerConsumer(), updaterFuture.completer());
        futures.add(updaterFuture);
    }

    @Test
    public void testVehicleUpdater(TestContext context) {
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put(com.wedriveu.shared.util.Constants.EventBus.BODY, "start");
            vertx.eventBus().consumer(ConstantsAnalytics.EventBus.VEHICLE_COUNTER_UPDATE,
                    msg -> {
                        VehicleUpdate update = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), VehicleUpdate.class);
                        assertTrue(update.getLicense().equals(ConstantsAnalytics.Messages.ANALYTICS_VEHICLE_TEST_LICENSE_PLATE) &&
                                update.getStatus().equals(Constants.Vehicle.STATUS_BOOKED));
                        async.complete();
                    });

            vertx.eventBus().send(ConstantsAnalytics.EventBus.TEST_VEHICLE_UPDATE, jsonObject);
        });
        async.awaitSuccess();
    }
}