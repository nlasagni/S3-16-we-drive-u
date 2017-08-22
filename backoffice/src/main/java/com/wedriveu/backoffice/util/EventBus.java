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

}
