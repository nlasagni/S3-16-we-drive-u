package com.wedriveu.services.analytics.util;

/**
 * @author Nicola Lasagni on 21/08/2017.
 * @author Stefano Bernagozzi on 31/08/2017.
 */
public interface ConstantsAnalytics {


    interface EventBus {

        /**
         * eventbus for starting a vehicle list request
         */
        String VEHICLE_LIST_REQUEST = "analytics.eventbus.VehicleListRequestVerticle";
        /**
         * eventbus for sending a vehicle list to the controller
         */
        String CONTROLLER_VEHICLE_LIST = "analytics.eventbus.AnalyticsVerticleController";
        /**
         * eventbus for testing the vehicle list request
         */
        String TEST_VEHICLE_LIST_REQUEST = "analytics.eventbus.test.vehicleListRequest";
        /**
         * eventbus for testing a vehicle update
         */
        String TEST_VEHICLE_UPDATE = "analytics.eventbus.test.vehicleUpdate";
        /**
         * eventbus for sending vehicle counter update to the controller
         */
        String VEHICLE_COUNTER_UPDATE = "analytics.eventbus.vehicleCounterUpdate";
        /**
         * eventbus for starting a vehicle counter request
         */
        String VEHICLE_COUNTER_REQUEST = "analytics.eventbus.vehicleCounterRequest";
        /**
         * eventbus for receive a vehicle counter after a request
         */
        String VEHICLE_COUNTER_RESPONSE = "analytics.eventbus.vehicleCounterResponse";
        /**
         * available address for rabbitmq consumer for retrieving the vehicle list
         */
        String AVAILABLE_ADDRESS_VEHICLE_LIST_RETRIEVER_VERTICLE =
                "analytics.eventbus.availableAddress.vehicleListRetriever";
        /**
         * available address for rabbitmq consumer for retrieving a vehicle update
         */
        String AVAILABLE_ADDRESS_VEHICLE_UPDATE_HANDLER =
                "analytics.eventbus.availableAddress.vehicleUpdateHandler";
        /**
         * available address for rabbitmq consumer for testing
         */
        String AVAILABLE_ADDRESS_FAKE_GENERATOR =
                "analytics.eventbus.availableAddress.fakeGenerator";
        /**
         * available address for rabbitmq consumer for retrieving a vehicle counter request
         */
        String AVAILABLE_ADDRESS_COUNTER_REQUEST =
                "analytics.eventbus.availableAddress.counterRequest";
        /**
         * available address for rabbitmq consumer for testing a vehicle counter request
         */
        String AVAILABLE_ADDRESS_FAKE_VEHICLE_COUNTER_REQUEST =
                "analytics.eventbus.availableAddress.fakeVehicleCounterRequest";
        /**
         * available address for rabbitmq consumer for testing a vehicle counter response
         */
        String AVAILABLE_ADDRESS_FAKE_VEHICLE_COUNTER_RESPONSE =
                "analytics.eventbus.availableAddress.fakeVehicleCounterResponse";
        /**
         * eventbus for testing a vehicle counter response
         */
        String FAKE_VEHICLE_COUNTER_RESPONSE_TEST =
                "analytics.eventbus.availableAddress.test.fakeVehicleCounterResponse";
        /**
         * eventbus for deploy the verticle for the exchange declaration
         */
        String SEND_VERTICLE_DEPLOYER =
                "analytics.eventbus.deployer.send";
        /**
         * eventbus for receiving the response of the exchange declaration
         */
        String RECEIVE_VERTICLE_DEPLOYER =
                "analytics.eventbus.deployer.receive";
        /**
         * eventbus for testing the vehicle list response
         */
        String TEST_VEHICLE_LIST_RESPONSE  = "analytics.eventbus.test.vehicleListResponse";
    }

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
