package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.analytics.vehicleService.VehicleCounterGeneratorRequestHandler;
import com.wedriveu.services.analytics.vehicleService.VehicleListGenerator;
import com.wedriveu.services.analytics.vehicleService.VehicleListGeneratorResponseHandler;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Context;
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

import static org.junit.Assert.*;

/**
 * @author Stefano Bernagozzi
 */
@RunWith(VertxUnitRunner.class)
public class AnalyticsVehicleRequestConsumerTest {
    private List<Future> futures;
    private Vertx vertx;
    private AnalyticsVehicleList vehicleListObject;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleRequestConsumer(), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleCounterGeneratorRequestHandler(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);

        List<Vehicle> vehicleList = VehicleListGenerator.getVehicleList();
        vehicleListObject = new AnalyticsVehicleList(vehicleList);
    }

    @Test
    public void testVehicleRequestConsumer(TestContext context){
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put(Constants.EventBus.BODY, "test");
            vertx.eventBus().send(EventBus.AVAILABLE_ADDRESS_FAKE_VEHICLE_COUNTER_REQUEST, jsonObject);
            vertx.eventBus().consumer(EventBus.VEHICLE_COUNTER_REQUEST,
                    msg -> {
                        JsonObject dataToUser = new JsonObject(msg.body().toString());
                        String backofficeId = dataToUser.getValue(Constants.EventBus.BODY).toString();
                        assertTrue(backofficeId.equals(EventBus.Messages.ANALYTICS_VEHICLE_COUNTER_TEST_BACKOFFICE_ID));
                        async.complete();
                    });
        });
        async.awaitSuccess();
    }

}