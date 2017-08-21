package com.wedriveu.services.analytics.boundary;


import com.wedriveu.services.analytics.vehicleService.VehicleListGeneratorRequestHandler;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.util.Position;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wedriveu.shared.util.Constants.ANALYTICS_CONTROLLER_VEHICLE_LIST_EVENTBUS;
import static com.wedriveu.shared.util.Constants.ANALYTICS_TEST_VEHICLE_LIST_REQUEST_EVENTBUS;
import static com.wedriveu.shared.util.Constants.VEHICLE_REQUEST_ALL_MESSAGE;
import static org.junit.Assert.*;

/**
 * @author Stefano Bernagozzi
 */
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
        vertx.deployVerticle(new VehicleListGeneratorRequestHandler(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);

        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle("MACCHINA1",
                Vehicle.STATUS_BROKEN_STOLEN,
                new Position(10.2, 13.2),
                new Date(2017, 11, 30, 12, 37, 43)));
        vehicleList.add(new Vehicle("MACCHINA2",
                Vehicle.STATUS_AVAILABLE,
                new Position(11.2, 14.2),
                new Date(2017, 10, 28, 11, 43, 12)));
        vehicleList.add(new Vehicle("MACCHINA3",
                Vehicle.STATUS_BOOKED,
                new Position(15.2, 13.2),
                new Date(2017, 9, 26, 10, 56, 46)));
        vehicleList.add(new Vehicle("MACCHINA4",
                Vehicle.STATUS_RECHARGING,
                new Position(13.2, 16.2),
                new Date(2017, 8, 24, 9, 37, 22)));

        vehicleListObject = new AnalyticsVehicleList(vehicleList);
    }


    @Test
    public void testRetrieve() {
        CompositeFuture.all(futures).setHandler(completed -> {

            JsonObject jsonObject = new JsonObject();
            jsonObject.put(Constants.EventBus.BODY,VEHICLE_REQUEST_ALL_MESSAGE);
            vertx.eventBus().send(ANALYTICS_TEST_VEHICLE_LIST_REQUEST_EVENTBUS, jsonObject);
            vertx.eventBus().consumer(ANALYTICS_CONTROLLER_VEHICLE_LIST_EVENTBUS,
                    msg->{
                        AnalyticsVehicleList vehicleList = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), AnalyticsVehicleList.class);
                        assertTrue(vehicleList.equals(vehicleListObject));
                    });

        });
    }

}