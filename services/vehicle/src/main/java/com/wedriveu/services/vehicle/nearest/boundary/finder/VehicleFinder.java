package com.wedriveu.services.vehicle.nearest.boundary.finder;

import com.wedriveu.services.shared.rabbitmq.RabbitMQConfig;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.shared.utilities.PositionUtils;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.entity.VehicleResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 * marco
 */
public class VehicleFinder extends VerticleConsumer {

    private static final String STARTED = "Started RabbitMQ publisher for eligible vehicle request";
    private static final String EXCHANGE_TYPE = "direct";
    private static final String EXCHANGE_DECLARED_LOG = Constants.VEHICLE_SERVICE_EXCHANGE + " exchange declared";
    private static final String MESSAGE_PUBLISHED_LOG = "Publisher sent message to ";
    private static RabbitMQClient client;
    private static String TAG = VehicleFinder.class.getSimpleName();
    private EventBus eventBus;
    private Double userLat;
    private Double userLon;
    private Double destinationLat;
    private Double destinationLon;
    private String username;
    private JsonArray availableVehicles;
    private Position userPosition;
    private Position destPosition;
    private int counter;

    private JsonArray responseJsonArray;
    private double distanceToUser;
    private double tripDistance;
    private JsonObject response;

    public VehicleFinder() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);

    }

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();

        eventBus.consumer(Messages.NearestControl.DATA_TO_VEHICLE, this::sendDataToVehicle);
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

        responseJsonArray = new JsonArray();


    }

    private void setupPublisher() {
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
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
        JsonObject dataToVehicle = getDataToVehicle(index);
        Log.info("DATA_TO_VEHICLE", dataToVehicle.encodePrettily());
        Log.info("EXCHANGE_NAME", exchangeName);
        Log.info("RUTING_KEY", routingKey);
        Log.info("INDEX",String.valueOf(index));
        client.basicPublish(exchangeName, routingKey, dataToVehicle, onPublish -> {
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
        distanceToUser = PositionUtils.getDistanceInKm(userPosition, vehiclePosition);
        tripDistance = (distanceToUser) + (PositionUtils.getDistanceInKm(userPosition, destPosition));
        JsonObject dataToVehicle = new JsonObject();
        dataToVehicle.put(Constants.TRIP_DISTANCE, tripDistance);
        /*
            the username is sent because if vehicle election asks to retreive it once vehicle data arrived,
            another username (from a new booking request) could be set in the meanwhile, creating data inconsistency
        * */
        // dataToVehicle.put(Constants.USER_USERNAME, username);
        return dataToVehicle;
    }



    public void startFinderConsumer() throws IOException, TimeoutException {

        startConsumer(onStart -> {
            declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    bindQueueToExchange(Constants.VEHICLE_SERVICE_EXCHANGE,
                            String.format(Constants.ROUTING_KEY_CAN_DRIVE_RESPONSE, username), onBind -> {
                                if (onBind.succeeded()) {
                                    registerConsumer(Constants.EVENT_BUS_FINDER_ADDRESS);
                                    basicConsume(Constants.EVENT_BUS_FINDER_ADDRESS);
                                }
                            });
                }
            });
        });
    }


    @Override
    public void registerConsumer(String eventBus) {
        counter ++;
        if(counter <= availableVehicles.size()){
            vertx.eventBus().consumer(eventBus, msg -> {
               /* JsonObject vehicleResponseJson = (JsonObject) msg.body();
                VehicleResponse vehicleResponse = vehicleResponseJson.mapTo(VehicleResponse.class);
                responseList.add(vehicleResponse);*/
                //responseJsonArray.add((JsonObject) msg.body());
                response = (JsonObject) msg.body();
                response.put(USER_USERNAME, username);
                response.put(DISTANCE_TO_USER, distanceToUser);
                responseJsonArray.add(response);

            });
        } else {


            // responseList.forEach(vehicleResponse -> new JsonArray().add(mapFrom(vehicleResponse)));
            vertx.eventBus().send(Messages.FinderConsumer.VEHICLE_RESPONSE, responseJsonArray);
            counter = 0;
            vertx.undeploy(deploymentID());
        }
    }


}

