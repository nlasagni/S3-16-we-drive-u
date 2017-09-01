package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.entity.MessageVehicleCounterWithID;
import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.analytics.vehicleService.VehicleListGenerator;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * @author Stefano Bernagozzi
 */
@RunWith(VertxUnitRunner.class)
public class AnalyticsVehicleDataManipulationVerticleTest {
    Vertx vertx;

    @Before
    public void setUp(TestContext context) throws Exception {
        Async async = context.async();
        vertx = Vertx.vertx();
        vertx.deployVerticle(new AnalyticsVehicleDataManipulationVerticle(), res-> {
            async.complete();
        });
        async.awaitSuccess();
    }

    @Test
    public void testInsertion(TestContext context) {
        Async async = context.async();
        vertx.eventBus().send(ConstantsAnalytics.EventBus.CONTROLLER_VEHICLE_LIST,
                VertxJsonMapper.mapInBodyFrom(
                        new AnalyticsVehicleList(VehicleListGenerator.getVehicleList())));
        vertx.eventBus().consumer(ConstantsAnalytics.EventBus.VEHICLE_COUNTER_RESPONSE, res->{
            MessageVehicleCounterWithID messageVehicleCounterWithID =
                    VertxJsonMapper.mapFromBodyTo((JsonObject) res.body(), MessageVehicleCounterWithID.class);
            assertTrue(VehicleListGenerator.getVehicleCounter().
                    equals(messageVehicleCounterWithID.getVehicleCounter()));
            async.complete();
        });
        async.awaitSuccess();
    }
}