package com.wedriveu.services.vehicle.app;

/**
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public interface Messages {

    interface NearestConsumer {
        String AVAILABLE = "consumer.available";
    }

    interface AvailableControl {
        String AVAILABLE_REQUEST = "control.available";

        String DATA_TO_VEHICLE = "control.data.to.vehicle";
    }


    interface FinderConsumer {

        String VEHICLE_RESPONSE = "finder.vehicle.response";
    }

    interface VehicleElection {
        String GET_VEHICLE = "election.vehicle.request";
    }

    interface VehicleStore {
        String AVAILABLE_COMPLETED = "store.available.completed";
        String GET_VEHICLE_COMPLETED = "store.get.vehicle.completed";

    }


}
