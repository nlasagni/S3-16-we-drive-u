package com.wedriveu.services.vehicle.available.boundary;

import com.wedriveu.services.shared.rabbitmq.BasicConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.app.UserData;
import com.wedriveu.services.vehicle.app.UserDataFactoryA;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.available.control.AvailableControlImpl;
import com.wedriveu.services.vehicle.entity.Vehicle;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by Marco on 30/07/2017.
 */
public class AvailableConsumerImpl extends BasicConsumer implements AvailableConsumer {
    ArrayList<Vehicle> available;

    private String userLat;
    private String userLon;
    private String destinationLat;
    private String destinationLon;
    private String username;


    public AvailableConsumerImpl() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }


    /*        FindVehicles finder = new FindVehiclesImpl();
        finder.listAllEligibleVehicles( new Position(43.158873, 13.720088),
                new Position(42.960979, 13.874647),
                available,
                new EligibleVehiclesControlImpl());*/



    @Override
    public void startVehicleService() throws IOException, TimeoutException {
        //createVehicles();
        setupRequests();
    }

    private void setupRequests() throws IOException, TimeoutException {
        BasicConsumer consumer = new AvailableConsumerImpl();
        consumer.start(onStart -> {
            consumer.declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    consumer.bindQueueToExchange(Constants.VEHICLE_SERVICE_EXCHANGE,
                            Constants.ROUTING_KEY_VEHICLE, onBind -> {
                        if (onBind.succeeded()) {
                            consumer.registerConsumer(Constants.EVENT_BUS_AVAILABLE_ADDRESS);
                            //consumer.basicConsume(Constants.EVENT_BUS_AVAILABLE_ADDRESS);

                            //questa è solo una prova, decommentare la riga sopra, cancellare userdata, userdatafactory ed emitter
                            UserData userData = new UserDataFactoryA().getUserData();
                            JsonObject messageObj = new JsonObject();
                            messageObj.put(Constants.USER_LATITUDE, userData.getUserLatitude());
                            messageObj.put(Constants.USER_LONGITUDE, userData.getUserLongitude());
                            messageObj.put(Constants.DESTINATION_LATITUDE, userData.getDestLatitude());
                            messageObj.put(Constants.DESTINATION_LONGITUDE, userData.getDestLongitude());
                            messageObj.put(Constants.USERNAME, userData.getUsername());
                            searchAvailableVehicles(messageObj);
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
            //JsonObject json = new JsonObject(Json.encode(getWraperObject(msg)));
            searchAvailableVehicles((JsonObject) msg.body());
        });
    }

    // mettere questo metodo in una classe di utility
    /*private DataWrapper getWraperObject(Message message) {
        JsonObject json = (JsonObject) message.body();
        userLat = json.getString(Constants.USER_LATITUDE);
        userLon = json.getString(Constants.USER_LONGITUDE);
        destinationLat = json.getString(Constants.DESTINATION_LATITUDE);
        destinationLon = json.getString(Constants.DESTINATION_LONGITUDE);
        username = json.getString(Constants.USER_USERNAME);
        DataWrapper dataWrapper = new DataWrapper();
        dataWrapper.setUserPosition(new Position(Double.parseDouble(userLat), Double.parseDouble(userLon)));
        dataWrapper.setDestinationPosition(new Position(Double.parseDouble(destinationLat), Double.parseDouble(destinationLon)));
        dataWrapper.setUsername(username);

        return dataWrapper;

    }*/

    private void searchAvailableVehicles(JsonObject json) {
        vertx.deployVerticle(new AvailableControlImpl(), managerDeployed -> {
            eventBus.send(Messages.NearestConsumer.AVAILABLE, json);
        });
    }

}
