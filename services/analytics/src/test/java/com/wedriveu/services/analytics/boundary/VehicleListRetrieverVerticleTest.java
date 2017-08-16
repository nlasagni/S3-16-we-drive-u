package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.vehicleServiceFake.VehicleListGeneratorRequestHandler;
import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.entity.VehicleListObject;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wedriveu.shared.util.Constants.*;
import static org.junit.Assert.assertTrue;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListRetrieverVerticleTest {
    private List<Future> futures;
    private Vertx vertx;
    private VehicleListObject vehicleListObject;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new VehicleListRetrieverVerticle(), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new VehicleListGeneratorRequestHandler(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);

        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle("MACCHINA1",
                "broken",
                new Position(10.2, 13.2),
                new Date(2017, 11, 30, 12, 37, 43)));
        vehicleList.add(new Vehicle("MACCHINA2",
                "available",
                new Position(11.2, 14.2),
                new Date(2017, 10, 28, 11, 43, 12)));
        vehicleList.add(new Vehicle("MACCHINA3",
                "busy",
                new Position(15.2, 13.2),
                new Date(2017, 9, 26, 10, 56, 46)));
        vehicleList.add(new Vehicle("MACCHINA4",
                "recharging",
                new Position(13.2, 16.2),
                new Date(2017, 8, 24, 9, 37, 22)));

        vehicleListObject = new VehicleListObject(vehicleList);


    }


    @Test
    public void testRetrieve() {
        CompositeFuture.all(futures).setHandler(completed -> {

            JsonObject jsonObject = new JsonObject();
            jsonObject.put(Constants.EventBus.BODY,VEHICLE_REQUEST_ALL_MESSAGE);
            vertx.eventBus().send(ANALYTICS_TEST_VEHICLE_LIST_REQUEST_EVENTBUS, jsonObject);
            vertx.eventBus().consumer(ANALYTICS_CONTROLLER_VEHICLE_LIST_VERTICLE_ADDRESS,
                    msg->{
                        VehicleListObject vehicleList = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), VehicleListObject.class);
                        assertTrue(vehicleList.equals(vehicleListObject));
                    });

        });
    }

}