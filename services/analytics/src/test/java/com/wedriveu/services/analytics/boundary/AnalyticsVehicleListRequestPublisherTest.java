package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.vehicleService.VehicleListGeneratorRequestHandler;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.wedriveu.shared.util.Constants.*;
import static org.junit.Assert.*;

/**
 * @author Stefano Bernagozzi
 */
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
    public void testRequest() {
        CompositeFuture.all(futures).setHandler(completed -> {
            vertx.eventBus().send(ANALYTICS_VEHICLE_LIST_REQUEST_EVENTBUS, ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE);
            vertx.eventBus().consumer(ANALYTICS_TEST_VEHICLE_LIST_REQUEST_EVENTBUS, msg->{
               String messageReceived = (String)((JsonObject) msg.body()).getValue(Constants.EventBus.BODY);
               assertTrue(messageReceived.equals(VEHICLE_REQUEST_ALL_MESSAGE));
            });

        });
    }


}