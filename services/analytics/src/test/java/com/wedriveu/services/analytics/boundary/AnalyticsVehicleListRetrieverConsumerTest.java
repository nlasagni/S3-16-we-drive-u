package com.wedriveu.services.analytics.boundary;


import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.analytics.vehicleService.VehicleListGenerator;
import com.wedriveu.services.analytics.vehicleService.VehicleListGeneratorResponseHandler;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
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
public class AnalyticsVehicleListRetrieverConsumerTest {

    private List<Future> futures;
    private Vertx vertx;
    private AnalyticsVehicleList vehicleListObject;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleListRetrieverConsumer(), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleListGeneratorResponseHandler(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);

        List<Vehicle> vehicleList = VehicleListGenerator.getVehicleList();
        vehicleListObject = new AnalyticsVehicleList(vehicleList);
    }


    @Test
    public void testRetrieve(TestContext context) {
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put(com.wedriveu.shared.util.Constants.EventBus.BODY, ConstantsAnalytics.Messages.ANALYTICS_VEHICLE_LIST_RESPONSE_START_MESSAGE_TEST);
            vertx.eventBus().send(ConstantsAnalytics.EventBus.TEST_VEHICLE_LIST_RESPONSE, jsonObject);
            vertx.eventBus().consumer(ConstantsAnalytics.EventBus.CONTROLLER_VEHICLE_LIST,
                    msg -> {
                        AnalyticsVehicleList vehicleList = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), AnalyticsVehicleList.class);
                        assertTrue(vehicleList.equals(vehicleListObject));
                        async.complete();
                    });
        });
        async.awaitSuccess();
    }

}