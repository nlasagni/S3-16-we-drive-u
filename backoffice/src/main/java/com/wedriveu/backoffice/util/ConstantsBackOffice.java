package com.wedriveu.backoffice.util;

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



    interface WebPage {

        /**
         * the first half of the google maps HTML page, before Javascript code
         */
        String HTML_WEB_PAGE_BEFORE_JAVASCRIPT_SCRIPT = "<!DOCTYPE html>" +
                "<html>" +
                "  <head>" +
                "    <style>" +
                "       #map {" +
                "        height: 600px;" +
                "        width: 600px;" +
                "       }" +
                "    </style>" +
                "  </head>" +
                "  <body>" +
                "    <div id=\"map\"></div>" +
                "    <script>" +
                "      function initMap() {" +
                "        var cesena = {lat: 44.1391000, lng: 12.2431500};" +
                "        var map = new google.maps.Map(document.getElementById('map'), {" +
                "          zoom: 4," +
                "          center: cesena" +
                "        });" ;
        /**
         * the second half of the google maps HTML page, after the javascript code
         */
        String HTML_WEB_PAGE_AFTER_JAVASCRIPT_SCRIPT =
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
