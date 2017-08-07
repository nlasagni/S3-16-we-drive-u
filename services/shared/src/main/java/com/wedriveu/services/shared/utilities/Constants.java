package com.wedriveu.services.shared.utilities;

/**
 * @author Michele on 22/07/2017.
 * @author Nicola Lasagni on 31/07/2017
 * @author Marco Baldassarri on 1/08/2017
 */
public interface Constants {

    String USERS_DATABASE_FILENAME = "users.json";
    String BOOKINGS_DATABASE_PATH = "services/booking/databaseFile/bookings.json";
    String VEHICLES_DATABASE_PATH = "services/vehicle/src/test/resources/vehicles.json";
    String BOOKING_DATABASE_FILE_NAME = "bookings.json";

    String USER_LOGIN = "/user/login";
    String USERNAME = "username";
    String PASSWORD = "password";
    String USERNAME_PASSWORD_MISSING = "username or password missing";
    String USERNAME_PASSWORD_WRONG = "wrong username or password";
    String USER_AUTHENTICATED = "user authenticated";
    String CONTENT_TYPE = "content-type";
    String TEXT_PLAIN = "text/plain";
    int PORT_AUTHENTICATION_SERVICE = 8080;

    String RABBITMQ_SERVER_HOST = "uniboguys.duckdns.org";
    String RABBITMQ_SERVER_PASSWORD = "FmzevdBBmpcdvPHLDJQR";
    int RABBITMQ_SERVER_PORT = 5672;
    String UTF = "UTF-8";


    double RANGE = 20;
    double MAXIMUM_DISTANCE_TO_RECHARGE = 20;
    double ESTIMATED_KILOMETERS_PER_PERCENTAGE = 10;
    String VEHICLE_TO_SERVICE = "ToService";

    // Vehicle Jackson parameters
    String CAR_LICENCE_PLATE = "carLicencePlate";
    String STATE = "state";
    String POSITION = "position";
    String LAST_UPDATE = "lastUpdate";

    // Services RabbitMQ
    String SERVICE_QUEUE_BASE_NAME = "service";
    String QUEUE_NAME_JSON_KEY = "queue";

    //Exchanges
    String VEHICLE_SERVICE_EXCHANGE = "vehicle";
    String DEPLOY_ERROR = "VERTICLE_DEPLOY_ERROR";

    // Vehicle Service RabbitMQ
    String EVENT_BUS_AVAILABLE_ADDRESS = "service.vehicle.eventbus";
    String EVENT_BUS_FINDER_ADDRESS = "finder.vehicle.eventbus";
    String CONSUMER_VEHICLE_SERVICE = "vehicle";
    String ROUTING_KEY_VEHICLE_REQUEST = "vehicle.request.nearest";
    String ROUTING_KEY_VEHICLE_RESPONSE = "vehicle.response.nearest.%s";
    String ROUTING_KEY_CAN_DRIVE = "vehicle.request.candrive.%s";
    String ROUTING_KEY_CAN_DRIVE_RESPONSE = "vehicle.response.candrive.%s";


    String BODY = "body";
    String USER_POSITION = "userPosition";
    String DESTINATION_POSITION = "destinationPosition";
    String ELIGIBLE = "eligible";
    String STATUS_AVAILABLE = "available";
    String ELIGIBLE_VEHICLE_LIST = "eligibleVehicleList";
    String TRIP_DISTANCE = "tripDistance";
    String DISTANCE_TO_USER = "distanceToUser";
    String VEHICLE_SPEED = "speed";

    String MESSAGE_PUBLISHED_LOG = "Publisher sent message to ";
    String EXCHANGE_TYPE = "direct";
    String STARTED = "Started RabbitMQ publisher for ";
    int ZERO = 0;
}
