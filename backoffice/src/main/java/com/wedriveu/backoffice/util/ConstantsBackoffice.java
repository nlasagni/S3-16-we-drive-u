package com.wedriveu.backoffice.util;

/**
 * @author Stefano Bernagozzi
 */
public interface ConstantsBackoffice {
    String TEST_BACKOFFICE_ID = "backoffice1test";

    interface WebPage {
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
        String BACKOFFICE_CONTROLLER_VEHICLES =
                "backoffice.eventbus.controller.vehicles";
        String BACKOFFICE_CONTROLLER_BOOKINGS =
                "backoffice.eventbus.controller.bookings";
        String AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_NO_ID =
                "backoffice.eventbus.availableAddress.rabbitmqListenerUpdateNoId";
        String AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_WITH_ID =
                "backoffice.eventbus.availableAddress.rabbitmqListenerUpdateWithId";
        String AVAILABLE_ADDRESS_BACKOFFICE_BOOKING_RESPONSE =
                "backoffice.eventbus.availableAddress.bookingResponse";
        String BACKOFFICE_BOOKING_LIST_REQUEST_CONTROLLER =
                "backoffice.eventbus.BookingListRequestController";
        String ANALYTICS_BOOKING_LIST_REQUEST_EVENTBUS_AVAILABLE_TEST =
                "backoffice.analytics.evenbus.available.test.bookingListRequest";
        String ANALYTICS_VEHICLE_COUNTER_REQUEST_EVENTBUS_AVAILABLE_TEST =
                "backoffice.analytics.evenbus.available.test.vehicleListRequest";
        String BACKOFFICE_BOOKING_LIST_REQUEST_TEST =
                "backoffice.eventbus.test.BookingListRequest";
        String BACKOFFICE_VEHICLE_COUNTER_REQUEST_TEST =
                "backoffice.eventbus.test.vehicleListRequest";
        String BACKOFFICE_VEHICLE_COUNTER_RESPONSE_GENERATOR_START_TEST =
                "backoffice.eventbus.test.vehicleCounterResponseGeneratorStart";
        String BACKOFFICE_BOOKING_LIST_RESPONSE_GENERATOR_START_TEST =
                "backoffice.eventbus.test.bookingListResponseGeneratorStart";

    }

    interface Queues {
        String ANALYTYCS_BOOKING_LIST_REQUEST_QUEUE_TEST =
                "backoffice.analytics.queue.test.bookingListRequest";
        String ANALYTYCS_VEHICLE_COUNTER_REQUEST_QUEUE_TEST =
                "backoffice.analytics.queue.test.vehicleListRequest";
        String ANALYTYCS_VEHICLE_COUNTER_RESPONSE_QUEUE_TEST =
                "backoffice.analytics.queue.test.vehicleCounterResponse";
    }


}
