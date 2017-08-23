package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.analytics.AnalyticsVehicleCounterResponseGenerator;
import com.wedriveu.backoffice.analytics.VehicleCounterGenerator;
import com.wedriveu.backoffice.util.EventBus;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
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

import static org.junit.Assert.*;

/**
 * @author Stefano Bernagozzi
 */
@RunWith(VertxUnitRunner.class)
public class BackofficeVehiclesResponseConsumerTest {
    private List<Future> futures;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new BackofficeVehiclesResponseConsumer(EventBus.ANALYTYCS_VEHICLE_COUNTER_RESPONSE_QUEUE_TEST, ""), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleCounterResponseGenerator(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);
    }

    @Test
    public void testVehicleResponsePublisher(TestContext context) {
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            JsonObject dataToUser = new JsonObject();
            dataToUser.put(Constants.EventBus.BODY, "requesting");
            vertx.eventBus().send(EventBus.BACKOFFICE_VEHICLE_COUNTER_RESPONSE_GENERATOR_START_TEST, dataToUser);
            vertx.eventBus().consumer(EventBus.BACKOFFICE_CONTROLLER_VEHICLES, res -> {
                VehicleCounter vehicleCounter =
                        VertxJsonMapper.mapFromBodyTo((JsonObject) res.body(), VehicleCounter.class);
                assertTrue(vehicleCounter.equals(VehicleCounterGenerator.getVehicleCounter()));
                async.complete();
            });
        });
        async.awaitSuccess();
    }

}