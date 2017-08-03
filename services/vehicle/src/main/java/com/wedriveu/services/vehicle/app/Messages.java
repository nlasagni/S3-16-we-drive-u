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
        String AVAILABLE_COMPLETED = "control.available.completed";
        String DATA_TO_VEHICLE = "control.data.to.vehicle";
    }


    interface FinderConsumer {
        String DISTANCE_TO_USER = "finder.distance.to.user";
        String TRIP_DISTANCE =  "finder.distance.trip";
    }
}
