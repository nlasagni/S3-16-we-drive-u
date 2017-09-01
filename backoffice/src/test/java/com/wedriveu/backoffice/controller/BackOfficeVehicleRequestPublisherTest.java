package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.analytics.AnalyticsVehicleCounterRequestConsumer;
import com.wedriveu.backoffice.util.ConstantsBackOffice;
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
public class BackOfficeVehicleRequestPublisherTest {
    private List<Future> futures;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new BackOfficeVehicleRequestPublisher(ConstantsBackOffice.TEST_BACKOFFICE_ID), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleCounterRequestConsumer(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);
    }

    @Test
    public void testVehicleRequestPublisher(TestContext context) {
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            vertx.eventBus().consumer(ConstantsBackOffice.EventBus.BACKOFFICE_VEHICLE_COUNTER_REQUEST_TEST, res -> {
                JsonObject dataFromUser = new JsonObject(res.body().toString());
                String backofficeIdReceived = dataFromUser.getValue(Constants.EventBus.BODY).toString();
                assertTrue(backofficeIdReceived.equals(ConstantsBackOffice.TEST_BACKOFFICE_ID));
                async.complete();
            });
        });
        async.awaitSuccess();
    }
}