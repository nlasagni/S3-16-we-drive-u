package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.vehicleService.VehicleUpdater;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
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
public class AnalyticsVehicleUpdateHandlerConsumerTest {
    private List<Future> futures;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new VehicleUpdater(), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future updaterFuture = Future.future();
        vertx.deployVerticle(new AnalyticsVehicleUpdateHandlerConsumer(),updaterFuture.completer());
        futures.add(updaterFuture);
    }

    @Test
    public void testVehicleUpdater() {
        CompositeFuture.all(futures).setHandler(completed -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put(Constants.EventBus.BODY,"start");
            vertx.eventBus().consumer(ANALYTCS_VEHICLE_COUNTER_UPDATE_EVENTBUS,
                    msg->{
                        UpdateToService update = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), UpdateToService.class);
                        assertTrue(update.getLicense().equals("Veicolo1") &&
                                    update.getStatus().equals("broken"));
                    });

            vertx.eventBus().send(Constants.ANALYTICS_EVENTBUS_AVAILABLE_ADDRESS_VEHICLE_UPDATE_HANDLER, jsonObject);

        });

    }
}