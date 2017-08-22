package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.analytics.vehicleService.VehicleListGeneratorRequestHandler;
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

import static com.wedriveu.shared.util.Constants.*;
import static org.junit.Assert.assertTrue;

/**
 * @author Stefano Bernagozzi
 */
@RunWith(VertxUnitRunner.class)
public class AnalyticsVehicleListRequestPublisherTest {
    private List<Future> futures;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future requestFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleListRequestPublisher(), requestFuture.completer());
        futures.add(requestFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleListGeneratorRequestHandler(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);
    }

    @Test
    public void testRequest(TestContext context) {
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            vertx.eventBus().send(EventBus.VEHICLE_LIST_REQUEST, EventBus.Messages.ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE);
            vertx.eventBus().consumer(EventBus.TEST_VEHICLE_LIST_REQUEST, msg -> {
                String messageReceived = (String) ((JsonObject) msg.body()).getValue(Constants.EventBus.BODY);
                assertTrue(messageReceived.equals(MessagesAnalytics.VEHICLE_REQUEST_ALL_MESSAGE));
                async.complete();
            });
        });
        async.awaitSuccess();
    }
}