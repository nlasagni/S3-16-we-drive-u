package com.wedriveu.services.analytics.util;

/**
 * @author Nicola Lasagni on 21/08/2017.
 */
public interface EventBus {

    String VEHICLE_LIST_REQUEST = "analytics.eventbus.VehicleListRequestVerticle";
    String CONTROLLER_VEHICLE_LIST = "analytics.eventbus.AnalyticsVerticleController";
    String TEST_VEHICLE_LIST_REQUEST = "analytics.eventbus.test.vehicleListRequest";
    String TEST_VEHICLE_UPDATE = "analytics.eventbus.test.vehicleUpdate";
    String VEHICLE_COUNTER_UPDATE = "analytics.eventbus.vehicleCounterUpdate";
    String VEHICLE_COUNTER_REQUEST = "analytics.eventbus.vehicleCounterRequest";
    String VEHICLE_COUNTER_RESPONSE = "analytics.eventbus.vehicleCounterResponse";
    String VEHICLE_STORE_UPDATE_REQUEST = "analytics.eventbus.vehicleStoreUpdate";
    String AVAILABLE_ADDRESS_VEHICLE_LIST_RETRIEVER_VERTICLE =
            "analytics.eventbus.availableAddress.vehicleListRetriever";
    String AVAILABLE_ADDRESS_VEHICLE_UPDATE_HANDLER =
            "analytics.eventbus.availableAddress.vehicleUpdateHandler";
    String AVAILABLE_ADDRESS_FAKE_GENERATOR =
            "analytics.eventbus.availableAddress.fakeGenerator";
    String AVAILABLE_ADDRESS_COUNTER_REQUEST =
            "analytics.eventbus.availableAddress.counterRequest";
    String AVAILABLE_ADDRESS_FAKE_VEHICLE_COUNTER_REQUEST =
            "analytics.eventbus.availableAddress.fakeVehicleCounterRequest";
    String AVAILABLE_ADDRESS_FAKE_VEHICLE_COUNTER_RESPONSE =
            "analytics.eventbus.availableAddress.fakeVehicleCounterResponse";
    String FAKE_VEHICLE_COUNTER_RESPONSE_TEST_EVENTBUS =
            "analytics.eventbus.availableAddress.test.fakeVehicleCounterResponse";
    String EVENTBUS_SEND_VERTICLE_DEPLOYER =
            "analytics.eventbus.deployer.send";
    String EVENTBUS_RECEIVE_VERTICLE_DEPLOYER =
            "analytics.eventbus.deployer.receive";

    String TEST_VEHICLE_LIST_RESPONSE  = "analytics.eventbus.test.vehicleListResponse";
    interface Messages {
        /**
         * The message sent inside the Analytics service to start requesting all vehicles.
         */
        String ANALYTICS_VEHICLE_LIST_REQUEST_START_MESSAGE = "Started all services, start requesting vehicle list";

        /**
         * The message sent inside the Analytics service to start testinge retrieving all vehicles.
         */
        String ANALYTICS_VEHICLE_LIST_RESPONSE_START_MESSAGE_TEST = "Started all services, start send vehicle list";

        /**
         * the backoffice id sent in the test to the analytics
         */

        String ANALYTICS_VEHICLE_COUNTER_TEST_BACKOFFICE_ID = "backoffice1test";

        /**
         * Vehicle test license plate
         */
        String ANALYTICS_VEHICLE_TEST_LICENSE_PLATE = "Vehicle1Test";


    }

}
