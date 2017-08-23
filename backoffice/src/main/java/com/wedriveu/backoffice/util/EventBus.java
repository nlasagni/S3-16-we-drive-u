package com.wedriveu.backoffice.util;

/**
 * @author Nicola Lasagni on 21/08/2017.
 */
public interface EventBus {

    String BACKOFFICE_CONTROLLER =
            "backoffice.eventbus.controller";
    String AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_NO_ID =
            "backoffice.eventbus.availableAddress.rabbitmqListenerUpdateNoId";
    String AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_WITH_ID =
            "backoffice.eventbus.availableAddress.rabbitmqListenerUpdateWithId";
    String AVAILABLE_ADDRESS_BACKOFFICE_BOOKING_RESPONSE =
            "backoffice.eventbus.availableAddress.bookingResponse";
    String BACKOFFICE_BOOKING_LIST_RETRIEVER_CONTROLLER =
            "backoffice.eventbus.BookingListRetrieverController";
    String BACKOFFICE_BOOKING_LIST_REQUEST_CONTROLLER =
            "backoffice.eventbus.BookingListRequestController";

}
