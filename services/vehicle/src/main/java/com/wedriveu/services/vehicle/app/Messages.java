package com.wedriveu.services.vehicle.app;

/**
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public interface Messages {

    interface NearestConsumer {
        String AVAILABLE = "consumer.available";
    }

    interface Store {
        String AVAILABLE_REQUEST = "store.available";
        String AVAILABLE_COMPLETED = "store.available.completed";
    }

    interface Manager {

        String AVAILABLE_VEHICLES = "manager.available.vehicles";
    }

    interface Finder {
        String USER_DATA = "finder.user.data";
    }
}
