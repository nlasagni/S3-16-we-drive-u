package com.wedriveu.services.vehicle.nearest.boundary.available;

import com.wedriveu.services.shared.rabbitmq.BasicConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.app.UserData;
import com.wedriveu.services.vehicle.app.UserDataFactoryA;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Marco on 30/07/2017.
 */
public class UserConsumer extends BasicConsumer {

    public UserConsumer() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }

    public void startUserConsumer() {
        BasicConsumer consumer = new UserConsumer();
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            //JsonObject json = new JsonObject(Json.encode(getWraperObject(msg)));
            searchAvailableVehicles((JsonObject) msg.body());
        });
    }

    private void searchAvailableVehicles(JsonObject userData) {
        eventBus.send(Messages.NearestConsumer.AVAILABLE, userData);
    }

}
