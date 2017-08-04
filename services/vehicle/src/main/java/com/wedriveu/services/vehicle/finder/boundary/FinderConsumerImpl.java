package com.wedriveu.services.vehicle.finder.boundary;

import com.wedriveu.services.shared.rabbitmq.BasicConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.app.UserData;
import com.wedriveu.services.vehicle.app.UserDataFactoryA;
import com.wedriveu.services.vehicle.available.control.AvailableControlImpl;
import com.wedriveu.services.vehicle.election.boundary.VehicleElection;
import com.wedriveu.services.vehicle.entity.Vehicle;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by Marco on 30/07/2017.
 */
public class FinderConsumerImpl extends BasicConsumer implements FinderConsumer {
    ArrayList<Vehicle> available;

    private String userLat;
    private String userLon;
    private String destinationLat;
    private String destinationLon;
    private String username;


    public FinderConsumerImpl() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }


    /*        FindVehicles finder = new FindVehiclesImpl();
        finder.listAllEligibleVehicles( new Position(43.158873, 13.720088),
                new Position(42.960979, 13.874647),
                available,
                new EligibleVehiclesControlImpl());*/



    @Override
    public void startFinderConsumer() throws IOException, TimeoutException {
        BasicConsumer consumer = new FinderConsumerImpl();
        consumer.start(onStart -> {
            consumer.declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    consumer.bindQueueToExchange(Constants.VEHICLE_SERVICE_EXCHANGE,
                            Constants.ROUTING_KEY_CAN_DRIVE_RESPONSE, onBind -> {
                                if (onBind.succeeded()) {
                                    consumer.registerConsumer(Constants.EVENT_BUS_FINDER_ADDRESS);
                                    consumer.basicConsume(Constants.EVENT_BUS_FINDER_ADDRESS);
                                }
                            });
                }
            });
        });
    }


    @Override
    public void stopVehicleService() {

    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            vertx.deployVerticle(new VehicleElection(), electionDeployed -> {
                vertx.eventBus().send(Messages.FinderConsumer.VEHICLE_RESPONSE, msg.body());
            });
        });
    }

}
