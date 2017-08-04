package com.wedriveu.services.vehicle.nearest.boundary.finder;

import com.wedriveu.services.shared.rabbitmq.RabbitMQClientConfig;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.app.Messages;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.services.shared.utilities.Constants.CAR_LICENCE_PLATE;
import static com.wedriveu.services.shared.utilities.Constants.POSITION;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 * marco
 */
public class VehicleFinderImpl extends AbstractVerticle {


    private static final String STARTED = "Started RabbitMQ publisher for eligible vehicle request";
    private static final String EXCHANGE_TYPE = "direct";
    private static final String EXCHANGE_DECLARED_LOG = Constants.VEHICLE_SERVICE_EXCHANGE + " exchange declared";
    private static final String MESSAGE_PUBLISHED_LOG = "Publisher sent message to ";
    private static RabbitMQClient client;
    private static String TAG = VehicleFinderImpl.class.getSimpleName();
    private EventBus eventBus;
    private Double userLat;
    private Double userLon;
    private Double destinationLat;
    private Double destinationLon;
    private String username;
    private JsonArray availableVehicles;
    private Position userPosition;
    private Position destPosition;


    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.AvailableControl.DATA_TO_VEHICLE, this::sendDataToVehicle);
    }

    private void sendDataToVehicle(Message message) {
        prepareData((JsonObject) message.body());
        setupPublisher();
    }

    private void prepareData(JsonObject body) {
        availableVehicles = body.getJsonArray(Constants.AVAILABLE_VEHICLES);
        userLat = Double.parseDouble(body.getString(Constants.USER_LATITUDE));
        userLon = Double.parseDouble(body.getString(Constants.USER_LONGITUDE));
        destinationLat = Double.parseDouble(body.getString(Constants.DESTINATION_LATITUDE));
        destinationLon = Double.parseDouble(body.getString(Constants.DESTINATION_LONGITUDE));
        username = body.getString(Constants.USER_USERNAME);

        userPosition = new Position(userLat, userLon);
        destPosition = new Position(destinationLat, destinationLon);


    }

    private void setupPublisher() {
        client = RabbitMQClientConfig.getInstance().getRabbitMQClient();
        client.start(onStartCompleted -> {
                    if (onStartCompleted.succeeded()) {
                        Log.info(TAG, STARTED);
                        declareExchanges(onDeclareCompleted -> {
                            if (onDeclareCompleted.succeeded()) {
                                Log.info(TAG, EXCHANGE_DECLARED_LOG);
                                for (int index = 0; index < availableVehicles.size(); index++) {
                                    publishToConsumer(Constants.VEHICLE_SERVICE_EXCHANGE,
                                            String.format(Constants.ROUTING_KEY_CAN_DRIVE,
                                                    availableVehicles.getJsonObject(index)
                                                            .getString(CAR_LICENCE_PLATE)), index);
                                }

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
                                   int index) {
        client.basicPublish(exchangeName, routingKey, getDataToVehicle(index), onPublish -> {
            if (onPublish.succeeded()) {
                Log.info(TAG, MESSAGE_PUBLISHED_LOG + exchangeName);
            } else {
                Log.error(TAG, onPublish.cause().getMessage(), onPublish.cause());
            }
        });
    }

    public JsonObject getDataToVehicle(int index) {
        Position vehiclePosition = availableVehicles.getJsonObject(index)
                .getJsonObject(POSITION)
                .mapTo(Position.class);
        double distanceToUser = userPosition.getDistanceInKm(vehiclePosition);
        double tripDistance = (distanceToUser) + (userPosition.getDistanceInKm(destPosition));
        JsonObject dataToVehicle = new JsonObject();
        dataToVehicle.put(Constants.TRIP_DISTANCE, tripDistance);
        /*
            the username is sent because if vehicle election asks to retreive it once vehicle data arrived,
            another username (from a new booking request) could be set in the meanwhile, creating data inconsistency
        * */
        dataToVehicle.put(Constants.USER_USERNAME, username);
        return dataToVehicle;
    }

}

