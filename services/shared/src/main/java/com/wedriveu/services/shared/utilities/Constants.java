package com.wedriveu.services.shared.utilities;

/**
 * @author Michele on 22/07/2017.
 * @author Nicola Lasagni on 31/07/2017
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

    String SERVER_HOST = "uniboguys.duckdns.org";
    String SERVER_PASSWORD = "FmzevdBBmpcdvPHLDJQR";
    String UTF = "UTF-8";


    double RANGE = 20;
    double MAXIMUM_DISTANCE_TO_RECHARGE = 20;
    double ESTIMATED_KILOMETERS_PER_PERCENTAGE = 10;
    String VEHICLE_TO_SERVICE = "ToService";
    String REQUEST_CAN_DO_JOURNEY = "Requesting if journey can be done";

    int FIRST_CHOSEN_ELIGIBLE_VEHICLE = 0;

}
