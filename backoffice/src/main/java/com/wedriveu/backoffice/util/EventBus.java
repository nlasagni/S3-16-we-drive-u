package com.wedriveu.backoffice.util;

/**
 * @author Nicola Lasagni on 21/08/2017.
 */
public interface EventBus {

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
    String ANALYTYCS_BOOKING_LIST_REQUEST_QUEUE_TEST =
            "backoffice.analytics.queue.test.bookingListRequest";
    String ANALYTYCS_VEHICLE_COUNTER_REQUEST_QUEUE_TEST =
            "backoffice.analytics.queue.test.vehicleListRequest";
    String ANALYTYCS_VEHICLE_COUNTER_RESPONSE_QUEUE_TEST =
            "backoffice.analytics.queue.test.vehicleCounterResponse";
    String BACKOFFICE_BOOKING_LIST_REQUEST_TEST =
            "backoffice.eventbus.test.BookingListRequest";
    String BACKOFFICE_VEHICLE_COUNTER_REQUEST_TEST =
            "backoffice.eventbus.test.vehicleListRequest";
    String BACKOFFICE_VEHICLE_COUNTER_RESPONSE_GENERATOR_START_TEST =
            "backoffice.eventbus.test.vehicleCounterResponseGeneratorStart";
    String TEST_BACKOFFICE_ID = "backoffice1test";


}
