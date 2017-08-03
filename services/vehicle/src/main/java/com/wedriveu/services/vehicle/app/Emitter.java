/*
package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.shared.rabbitmq.RabbitMQClientConfig;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

*/
/**
 * Created by Marco on 03/08/2017.
 *//*

public class Emitter {

    private static final String STARTED = "Started RabbitQM client";
    private static final String DECLARED_EXCHANGE = "Declared exchange ";
    private static final String EXCHANGE_TYPE = "direct";
    private static final String EXCHANGE_DECLARED = Constants.VEHICLE_SERVICE_EXCHANGE + " exchange declared";
    private static RabbitMQClient client;
    private static String TAG = Emitter.class.getSimpleName();

    public static void main(String[] args) throws IOException, TimeoutException {
        client = RabbitMQClientConfig.getInstance().getRabbitMQClient();
        client.start(onStartCompleted -> {
                    if (onStartCompleted.succeeded()) {
                        Log.info(TAG, STARTED);
                        declareExchanges(onDeclareCompleted -> {
                            if (onDeclareCompleted.succeeded()) {
                                Log.info(TAG, EXCHANGE_DECLARED);
                                UserData userDataA = new UserDataFactoryA().getUserData();
                                publishToConsumer(Constants.VEHICLE_SERVICE_EXCHANGE, Constants.ROUTING_KEY_VEHICLE, userDataA);
                            } else {
                                Log.error(TAG, onDeclareCompleted.cause().getMessage(), onDeclareCompleted.cause());
                            }
                        });
                    } else {
                        Log.error(TAG, onStartCompleted.cause().getMessage(), onStartCompleted.cause());
                    }
                }
        );
    }

    private static void declareExchanges(Handler<AsyncResult<Void>> handler) {
        client.exchangeDeclare(Constants.VEHICLE_SERVICE_EXCHANGE,
                EXCHANGE_TYPE,
                false,
                false,
                handler);
    }

    private static void publishToConsumer(String exchangeName,
                                          String routingKey,
                                          UserData userData) {
        JsonObject messageObj = new JsonObject();
        messageObj.put(Constants.USER_LATITUDE, userData.getUserLatitude());
        messageObj.put(Constants.USER_LONGITUDE, userData.getUserLongitude());
        messageObj.put(Constants.DESTINATION_LATITUDE, userData.getDestLatitude());
        messageObj.put(Constants.DESTINATION_LONGITUDE, userData.getDestLongitude());
        client.basicPublish(exchangeName, routingKey, messageObj, onPublish -> {
            if (onPublish.succeeded()) {
                Log.info(TAG, "Publisher sent message to " + Constants.CONSUMER_VEHICLE_SERVICE);
            } else {
                Log.error(TAG, onPublish.cause().getMessage(), onPublish.cause());
            }
        });
    }

}
*/
