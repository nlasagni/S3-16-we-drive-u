package com.wedriveu.services.shared.utilities;

/**
 * Created by Michele on 17/07/2017.
 */
public class Util {

    public final static String USERS_DATABASE_PATH = "services/authentication/databaseFile/users.json";
    public final static String BOOKINGS_DATABASE_PATH = "services/booking/databaseFile/bookings.json";
    public final static String VEHICLES_DATABASE_PATH = "services/vehicle/databaseFile/vehicles.json";

    public static final String USER_LOGIN = "/user/login";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USERNAME_PASSWORD_MISSING = "username or password missing";
    public static final String USERNAME_PASSWORD_WRONG = "wrong username or password";
    public static final String USER_AUTHENTICATED = "user authenticated";
    public static final String CONTENT_TYPE = "content-type";
    public static final String TEXT_PLAIN = "text/plain";
    public static final int PORT_AUTHENTICATION_SERVICE = 8080;

    public static final String SERVER_HOST = "uniboguys.duckdns.org";
    public static final String SERVER_PASSWORD = "FmzevdBBmpcdvPHLDJQR";
    public static final String UTF = "UTF-8";


    public static final double RANGE = 20;
    public static final double MAXIMUM_DISTANCE_TO_RECHARGE = 20;
    public static final double ESTIMATED_KILOMETERS_PER_PERCENTAGE = 10;
    public static final String VEHICLE_TO_SERVICE = "ToService";
    public static final String REQUEST_CAN_DO_JOURNEY = "Requesting if journey can be done";


    public static void log(String toLog){
        System.out.println(toLog);
    }


}
