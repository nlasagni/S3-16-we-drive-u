package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.entity.AnalyticsStore;
import com.wedriveu.services.analytics.entity.AnalyticsStoreImpl;
import com.wedriveu.services.analytics.entity.MessageVehicleCounterWithID;
import com.wedriveu.services.analytics.entity.VehiclesCounterAlgorithmImpl;
import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.analytics.vehicleService.VehicleCounterGeneratorResponseHandler;
import com.wedriveu.services.analytics.vehicleService.VehicleListGenerator;
import com.wedriveu.services.shared.model.AnalyticsVehicle;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.store.JsonFileEntityListStoreStrategyImpl;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
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
public class AnalyticsVehiclesResponsePublisherTest {

    private AnalyticsStore analyticsStore;
    private List<Future> futures;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        analyticsStore = new AnalyticsStoreImpl(
                new JsonFileEntityListStoreStrategyImpl<>(AnalyticsVehicle.class, "analyticsTest.json"),
                new VehiclesCounterAlgorithmImpl());
        analyticsStore.clear();
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehiclesResponsePublisher(), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleCounterGeneratorResponseHandler(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);

        List<Vehicle> vehicleList = VehicleListGenerator.getVehicleList();
        for (Vehicle vehicle : vehicleList) {
            analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        }
    }

    @Test
    public void testVehicleResponsePublisher(TestContext context) {
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            vertx.eventBus().send(ConstantsAnalytics.EventBus.VEHICLE_COUNTER_RESPONSE,
                    VertxJsonMapper.mapInBodyFrom(new MessageVehicleCounterWithID(
                            ConstantsAnalytics.Messages.ANALYTICS_VEHICLE_COUNTER_TEST_BACKOFFICE_ID,
                            analyticsStore.getVehicleCounter())));
            vertx.eventBus().consumer(ConstantsAnalytics.EventBus.FAKE_VEHICLE_COUNTER_RESPONSE_TEST, res-> {
                VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) res.body(), VehicleCounter.class);
                assertTrue(vehicleCounter.equals(analyticsStore.getVehicleCounter()));
                async.complete();
            });
        });
        async.awaitSuccess();
    }
}