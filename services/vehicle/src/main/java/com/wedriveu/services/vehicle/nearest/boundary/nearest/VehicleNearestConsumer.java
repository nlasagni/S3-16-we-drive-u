package com.wedriveu.services.vehicle.nearest.boundary.nearest;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.app.UserData;
import com.wedriveu.services.vehicle.app.UserDataFactoryA;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Starts the consumer which handles the user request for the nearest vehicle.
 * The objects the consumer expects to receive are the user current position (Latitude, Longitude), the chosen
 * destination position (Latitude, Longitude) and the username associated with the user.
 *
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public class VehicleNearestConsumer extends VerticleConsumer {

    @Override
    public void start() throws Exception {
        super.start();
        startUserConsumer();
    }

    public VehicleNearestConsumer() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }

    public void startUserConsumer() {
        try {
            startConsumer(onStart -> {
                declareQueue(onQueue -> {
                    if (onQueue.succeeded()) {
                        bindQueueToExchange(Constants.VEHICLE_SERVICE_EXCHANGE,
                                Constants.ROUTING_KEY_VEHICLE, onBind -> {
                                    if (onBind.succeeded()) {
                                        registerConsumer(Constants.EVENT_BUS_AVAILABLE_ADDRESS);
                                        //consumer.basicConsume(Constants.EVENT_BUS_AVAILABLE_ADDRESS);
                                        //questa è solo una prova, decommentare la riga sopra, cancellare userdata, userdatafactory ed emitter
                                        UserData userData = new UserDataFactoryA().getUserData();
                                        JsonObject messageObj = new JsonObject();
                                        messageObj.put(Constants.USER_LATITUDE, userData.getUserLatitude());
                                        messageObj.put(Constants.USER_LONGITUDE, userData.getUserLongitude());
                                        messageObj.put(Constants.DESTINATION_LATITUDE, userData.getDestLatitude());
                                        messageObj.put(Constants.DESTINATION_LONGITUDE, userData.getDestLongitude());
                                        messageObj.put(Constants.USER_USERNAME, userData.getUsername());
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
       /* vertx.deployVerticle(new ControlImpl(), completed -> {
            if(completed.succeeded()){*/
                eventBus.send(Messages.UserNearestConsumer.AVAILABLE, userData);
           /* } else {
                Log.error(Constants.DEPLOY_ERROR, UserNearestConsumer.class.getSimpleName(), completed.cause());
            }

        });*/

    }

}
