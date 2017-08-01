package com.wedriveu.services.vehicle;

import com.wedriveu.services.shared.rabbitmq.BasicConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.finder.control.VehicleFinder;
import com.wedriveu.services.vehicle.finder.control.VehicleFinderImpl;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * Created by Marco on 30/07/2017.
 */
public class VehicleServiceConsumerImpl extends BasicConsumer implements VehicleServiceConsumer {
    ArrayList<Vehicle> available;
    private String userLat;
    private String userLon;
    private String destinationLat;
    private String destinationLon;
    private String username;

    public VehicleServiceConsumerImpl() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }


    /*        FindVehicles finder = new FindVehiclesImpl();
        finder.listAllEligibleVehicles( new Position(43.158873, 13.720088),
                new Position(42.960979, 13.874647),
                available,
                new EligibleVehiclesControlImpl());*/



    @Override
    public void startVehicleService() throws IOException, TimeoutException {
        createVehicles();
        setupRequests();
    }

    private void setupRequests() throws IOException, TimeoutException {
        BasicConsumer consumer = new VehicleServiceConsumerImpl();
        consumer.start(onStart -> {
            consumer.declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    consumer.bindQueueToExchange(Constants.VEHICLE_SERVICE_EXCHANGE,
                            Constants.ROUTING_KEY_VEHICLE, onBind -> {
                        if (onBind.succeeded()) {
                            consumer.registerConsumer(Constants.VEHICLE_SERVICE_EVENT_BUS);
                            consumer.basicConsume(Constants.VEHICLE_SERVICE_EVENT_BUS);
                        }
                    });
                }
            });
        });
    }


    @Override
    public void deployVehicleFinder() throws IOException, TimeoutException {

        VehicleFinder vehicleFinder = new VehicleFinderImpl();
        vertx.deployVerticle((Verticle) vehicleFinder, finderDeployed ->
                this.eventBus.send(Message.Finder.VEHICLES, available));
        //TODO wrappare in un unico json object i veicoli e i dati presi dall'utente
    }


    private void createVehicles() {
        available = new ArrayList<>();
        available.add(new Vehicle("veicolo1",
                "available",
                new Position(44.139644, 12.246429), //farthest vehicle
                new Date()));
        available.add(new Vehicle("veicolo2", //shortest vehicle
                "available",
                new Position(43.111162, 13.603608),
                new Date()));
        available.add(new Vehicle("veicolo3",
                "available",
                new Position(10, 10),
                new Date()));
    }

    @Override
    public void stopVehicleService() {

    }


    @Override
    public void registerConsumer(String eventBus) {
        // Create the event bus handler which messages will be sent to
        vertx.eventBus().consumer(eventBus, msg -> {
            JsonObject json = (JsonObject) msg.body();
            userLat = json.getString(Constants.USER_LATITUDE);
            userLon = json.getString(Constants.USER_LONGITUDE);
            destinationLat = json.getString(Constants.DESTINATION_LATITUDE);
            destinationLon = json.getString(Constants.DESTINATION_LONGITUDE);
            username = json.getString(Constants.USER_USERNAME);
        });
    }
}
