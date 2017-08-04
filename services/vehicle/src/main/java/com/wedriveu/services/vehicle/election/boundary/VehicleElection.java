package com.wedriveu.services.vehicle.election.boundary;

import com.wedriveu.services.shared.rabbitmq.RabbitMQClientConfig;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.finder.control.VehicleFinder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.services.shared.utilities.Constants.CAR_LICENCE_PLATE;
import static com.wedriveu.services.shared.utilities.Constants.USER_USERNAME;

/**
 * Created by Marco on 04/08/2017.
 */
public class VehicleElection extends AbstractVerticle {


    private static final String STARTED = "Started RabbitMQ publisher for user response";
    private static final String EXCHANGE_TYPE = "direct";
    private static final String EXCHANGE_DECLARED_LOG = Constants.VEHICLE_SERVICE_EXCHANGE + " exchange declared";
    private static final String MESSAGE_PUBLISHED_LOG = "Publisher sent message to ";
    private static RabbitMQClient client;
    private static String TAG = VehicleFinder.class.getSimpleName();
    private String destinationTime;
    private String pickupTime;

    @Override
    public void start() throws Exception {


        vertx.eventBus().consumer(Messages.FinderConsumer.VEHICLE_RESPONSE, this::retreiveVehicle);
        vertx.eventBus().consumer(Messages.VehicleStore.GET_VEHICLE_COMPLETED, this::sendVehicleToUser);

    }

    private void retreiveVehicle(Message message) {
        JsonObject vehicleResponse = new JsonObject();
        /*pickupTime = vehicleResponse.getString(Constants.PICK_UP_TIME);
        destinationTime = vehicleResponse.getString(Constants.ARRIVE_AT_DESTINATION_TIME);*/
        vertx.eventBus().send(Messages.VehicleElection.GET_VEHICLE, message.body());
    }

    private void sendVehicleToUser(Message message) {

        JsonObject dataToUser = new JsonObject();
        /*
        after this the object should contain vehicle, pickuptime, arriveatdestinationtime, username
        * */
        dataToUser.put(Constants.ELECTED_VEHICLE, (JsonObject) message.body());
        dataToUser.remove(Constants.CAR_LICENCE_PLATE);
        /*dataToUser.put(Constants.PICK_UP_TIME, pickupTime);
        dataToUser.put(Constants.ARRIVE_AT_DESTINATION_TIME, destinationTime);*/
        client = RabbitMQClientConfig.getInstance().getRabbitMQClient();
        client.start(onStartCompleted -> {
                    if (onStartCompleted.succeeded()) {
                        Log.info(TAG, STARTED);
                        declareExchanges(onDeclareCompleted -> {
                            if (onDeclareCompleted.succeeded()) {
                                Log.info(TAG, EXCHANGE_DECLARED_LOG);
                                publishToConsumer(Constants.VEHICLE_SERVICE_EXCHANGE,
                                        String.format(Constants.ROUTING_KEY_VEHICLE_RESPONSE,
                                                dataToUser.getString(USER_USERNAME)), dataToUser);
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


    private void declareExchanges(Handler<AsyncResult<Void>> handler) {
        client.exchangeDeclare(Constants.VEHICLE_SERVICE_EXCHANGE,
                EXCHANGE_TYPE,
                false,
                false,
                handler);
    }

    private void publishToConsumer(String exchangeName,
                                   String routingKey,
                                   JsonObject dataToUser) {
        client.basicPublish(exchangeName, routingKey, dataToUser, onPublish -> {
            if (onPublish.succeeded()) {
                Log.info(TAG,  MESSAGE_PUBLISHED_LOG + exchangeName);
            } else {
                Log.error(TAG, onPublish.cause().getMessage(), onPublish.cause());
            }
        });
    }
}
