package com.wedriveu.mobile.util;

/**
 * Global constants for the mobile app.
 *
 * @author Nicola Lasagni on 18/07/2017.
 */
public interface Constants {

    /**
     * The {@linkplain android.os.Bundle} key for a generic view model id value.
     */
    String VIEW_MODEL_ID = "viewModelId";

    /**
     * The code needed to recognize a PLACE_AUTOCOMPLETE request to GoogleApi.
     */
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    /**
     * The timeout for services operations.
     */
    int SERVICE_OPERATION_TIMEOUT = 30000;

    /**
     * A message to be displayed or logged when an empty response is received or the timeout is expired.
     */
    String NO_RESPONSE_DATA_ERROR = "An error occurred while communicating with our service, please try again later.";
    /**
     * A message to be displayed or logged when an error occurred during communication closing.
     */
    String CLOSE_COMMUNICATION_ERROR = "Error occurred while closing RabbitMQ communication.";

    /**
     * Constants related to the RabbitMQ queues.
     */
    interface Queue {
        /**
         * The user queue name, must be completed with the username,
         * see {@linkplain String#format(String, Object...)}.
         */
        String USER = "user.%s";
        /**
         * The user queue name, must be completed with the username,
         * see {@linkplain String#format(String, Object...)}.
         */
        String VEHICLE = "vehicle.%s";
        /**
         *
         */
        String BOOKING = "booking.%s";
    }

}
