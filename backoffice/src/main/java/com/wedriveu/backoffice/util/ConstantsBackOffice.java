package com.wedriveu.backoffice.util;

import static com.wedriveu.shared.util.Constants.HEAD_QUARTER;

/**
 * @author Stefano Bernagozzi
 */
public interface ConstantsBackOffice {
    /**
     * constants for testing
     */
    String TEST_BACKOFFICE_ID = "backoffice1test";
    int VEHICLE_AVAILABLE = 2;
    int VEHICLE_BOOKED = 3;
    int VEHICLE_BROKEN = 1;
    int VEHICLE_NETWORK_ISSUES = 0;
    int VEHICLE_RECHARGING = 0;

    /**
     * string for setting window title of the browser window
     */
    String BROWSER_TITLE = "Booking Positions";
    /**
     * string for appending updates to a queue name
     */
    String UPDATES_CONSTANT = ".updates";
    /**
     * string for creating the base of a routing key with dot between two string and a dot at the end of them
     */
    String ROUTING_KEY_BASE_FORMAT = "%s.%s.";

    /**
     * string for setting the label of the car counter
     */
    String LABEL_TEXT = "<html><body>" +
            "Available: %s<br>" +
            "Broken: %s<br>" +
            "Booked: %s<br>" +
            "Recharging: %s<br>" +
            "Network Issues: %s<br>" +
            "</body></html>";


    interface WebPage {
        /**
         * javascript code for creating a new red google maps marker with 2 positions given by the user
         */
        String JAVASCRIPT_MARKER_CODE_STARTING_POSITION = "new google.maps.Marker({position: {lat: %s"+
                ", lng: %s },map: map," +
                "icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png'});";
        /**
         * javascript code for creating a new green google maps marker with 2 positions given by the user
         */
        String JAVASCRIPT_MARKER_CODE_DESTINATION_POSITION = "new google.maps.Marker({position: {lat: %s"+
                ", lng: %s },map: map," +
                "icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'});";

        /**
         * the google maps HTML page
         */
        String HTML_WEB_PAGE_CODE = "<!DOCTYPE html>" +
                "<html>" +
                "  <head>" +
                "    <style>" +
                "       #map {" +
                "        height: 570px;" +
                "        width: 600px;" +
                "       }" +
                "    </style>" +
                "  </head>" +
                "  <body>" +
                "Legend: <img src='http://maps.google.com/mapfiles/ms/icons/red-dot.png'> user position" +
                " <img src='http://maps.google.com/mapfiles/ms/icons/green-dot.png'> destination position" +
                "    <div id=\"map\"></div>" +
                "    <script>" +
                "      function initMap() {" +
                "        var cesena = {lat: " +
                HEAD_QUARTER.getLatitude() +
                ", lng: " +
                HEAD_QUARTER.getLongitude() +
                "};" +
                "        var map = new google.maps.Map(document.getElementById('map'), {" +
                "          zoom: 9," +
                "          center: cesena," +
                "          mapTypeControl: false" +
                "        });" +
                "%s" +
                "}" +
                "    </script>" +
                "    <script async defer" +
                "    src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyBDwD2anM-m3WG705nwIrvyMuYGoWpLlBY&callback=initMap\">" +
                "    </script>" +
                "  </body>" +
                "</html>";
    }

    interface EventBus {
        /**
         * backoffice eventbus for sending vehicle counter to the controller
         */
        String BACKOFFICE_CONTROLLER_VEHICLES =
                "backoffice.eventbus.controller.vehicles";
        /**
         * backoffice eventbus for sending booking list to the controller
         */
        String BACKOFFICE_CONTROLLER_BOOKINGS =
                "backoffice.eventbus.controller.bookings";
        /**
         * available address inside the vehicle counter update listener for rabbitMQ comsumer, for the update to all the backoffices
         */
        String AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_NO_ID =
                "backoffice.eventbus.availableAddress.rabbitmqListenerUpdateNoId";
        /**
         * available address inside the vehicle counter update listener for rabbitMQ comsumer
         */
        String AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_WITH_ID =
                "backoffice.eventbus.availableAddress.rabbitmqListenerUpdateWithId";
        /**
         * available address for the rabbitMQ consumer for the booking list retriever
         */
        String AVAILABLE_ADDRESS_BACKOFFICE_BOOKING_RESPONSE =
                "backoffice.eventbus.availableAddress.bookingResponse";
        /**
         * eventbus for starting a booking list request
         */
        String BACKOFFICE_BOOKING_LIST_REQUEST_CONTROLLER =
                "backoffice.eventbus.BookingListRequestController";
        /**
         * eventbus for testing the booking list request
         */
        String ANALYTICS_BOOKING_LIST_REQUEST_EVENTBUS_AVAILABLE_TEST =
                "backoffice.analytics.evenbus.available.test.bookingListRequest";
        /**
         * eventbus for testing the vehicle counter request
         */
        String ANALYTICS_VEHICLE_COUNTER_REQUEST_EVENTBUS_AVAILABLE_TEST =
                "backoffice.analytics.evenbus.available.test.vehicleListRequest";
        /**
         * eventbus for testing the booking list request
         */
        String BACKOFFICE_BOOKING_LIST_REQUEST_TEST =
                "backoffice.eventbus.test.BookingListRequest";
        /**
         * eventbus for testing the vehicle counter request
         */
        String BACKOFFICE_VEHICLE_COUNTER_REQUEST_TEST =
                "backoffice.eventbus.test.vehicleListRequest";
        /**
         * eventbus for testing the vehicle counter response
         */
        String BACKOFFICE_VEHICLE_COUNTER_RESPONSE_GENERATOR_START_TEST =
                "backoffice.eventbus.test.vehicleCounterResponseGeneratorStart";
        /**
         * eventbus for testing the booking list response
         */
        String BACKOFFICE_BOOKING_LIST_RESPONSE_GENERATOR_START_TEST =
                "backoffice.eventbus.test.bookingListResponseGeneratorStart";

    }

    interface Queues {
        /**
         * queue for testing the booking list
         */
        String ANALYTYCS_BOOKING_LIST_REQUEST_QUEUE_TEST =
                "backoffice.analytics.queue.test.bookingListRequest";
        /**
         * queue for testing the vehiccle counter request
         */
        String ANALYTYCS_VEHICLE_COUNTER_REQUEST_QUEUE_TEST =
                "backoffice.analytics.queue.test.vehicleListRequest";
        /**
         * queue for testing the vehicle counter response
         */
        String ANALYTYCS_VEHICLE_COUNTER_RESPONSE_QUEUE_TEST =
                "backoffice.analytics.queue.test.vehicleCounterResponse";
    }


}
