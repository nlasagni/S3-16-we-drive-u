package com.wedriveu.services.vehicle.finder.control;

import com.wedriveu.services.shared.rabbitmq.RabbitMQClientConfig;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.finder.boundary.CommunicationWithVehicles;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.shared.utilities.Constants.CAR_LICENCE_PLATE;
import static com.wedriveu.services.shared.utilities.Constants.POSITION;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 */
public class VehicleFinderImpl extends AbstractVerticle implements VehicleFinder {


    private CommunicationWithVehicles communicationWithVehicles;
    private EventBus eventBus;

    private Double userLat;
    private Double userLon;
    private Double destinationLat;
    private Double destinationLon;
    private String username;
    private JsonArray availableVehicles;

    private static final String STARTED = "Started RabbitMQ publisher for eligible vehicle request";
    private static final String EXCHANGE_TYPE = "direct";
    private static final String EXCHANGE_DECLARED_LOG = Constants.VEHICLE_SERVICE_EXCHANGE + " exchange declared";
    private static final String MESSAGE_PUBLISHED_LOG = "Publisher sent message to ";
    private static RabbitMQClient client;
    private static String TAG = VehicleFinder.class.getSimpleName();

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
                                for(int index = 0; index < availableVehicles.size(); index++) {
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
        client.basicPublish(exchangeName, routingKey, getDistances(index), onPublish -> {
            if (onPublish.succeeded()) {
                Log.info(TAG,  MESSAGE_PUBLISHED_LOG + exchangeName);
            } else {
                Log.error(TAG, onPublish.cause().getMessage(), onPublish.cause());
            }
        });
    }

    public JsonObject getDistances(int index) {
        Position vehiclePosition = availableVehicles.getJsonObject(index)
                                                    .getJsonObject(POSITION)
                                                    .mapTo(Position.class);
        double distanceToUser = userPosition.getDistanceInKm(vehiclePosition);
        double tripDistance = (distanceToUser) + (userPosition.getDistanceInKm(destPosition));
        JsonObject distances = new JsonObject();
        distances.put(Messages.FinderConsumer.DISTANCE_TO_USER, distanceToUser);
        distances.put(Messages.FinderConsumer.TRIP_DISTANCE, tripDistance);
        return distances;
    }

    public VehicleFinderImpl() throws IOException, TimeoutException {
        // communicationWithVehicles = new CommunicationWithVehiclesImpl();
       /* listAllEligibleVehicles( new Position(43.158873, 13.720088),
                new Position(42.960979, 13.874647),
                available,
                new EligibleVehiclesControlImpl());*/
    }

   /* public void listAllEligibleVehicles(Position userPosition,
                                        Position destPosition,
                                        List<Vehicle> allAvailable,
                                        FindVehiclesCallback callback) throws IOException {
        CounterVehiclesEligibles counter = new CounterVehiclesEligibles();
        List<EligibleVehicle> eligibles = new ArrayList<>();
        for (Vehicle current : allAvailable) {
            if (isInRange(userPosition, current.getPosition())) {
                double distanceToUser = userPosition.getEuclideanDistance(current.getPosition());
                double tripDistance = (distanceToUser) + (userPosition.getEuclideanDistance(destPosition));
                counter.addCalled();
                communicationWithVehicles.requestCanDoJourney(current.getCarLicencePlate(), tripDistance, canDo -> {
                    if (canDo) {
                        eligibles.add(new EligibleVehicle(current, distanceToUser));
                    }
                    counter.addFinished();
                });
            }
        }
        new Thread(() -> {
            try {
                while (!counter.isFinished()) {
                }
                callback.listAllEligiblesVehiclesCallback(eligibles);
            } catch (Exception v) {
                v.printStackTrace();
            }
        }).start();
    }

    private boolean isInRange(Position userPosition, Position vehiclePosition) {
        return userPosition.getEuclideanDistance(vehiclePosition) < Constants.RANGE;
    }*/

}

