package com.wedriveu.services.analytics.util;

/**
 * @author Nicola Lasagni on 21/08/2017.
 */
public interface EventBus {

    String VEHICLE_LIST_REQUEST = "analytics.eventbus.VehicleListRequestVerticle";
    String CONTROLLER_VEHICLE_LIST = "analytics.eventbus.AnalyticsVerticleController";
    String TEST_VEHICLE_LIST_REQUEST = "analytics.eventbus.test.vehicleListRequest";
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
    
}
