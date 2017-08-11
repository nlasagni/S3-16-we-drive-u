package com.wedriveu.services.vehicle.rabbitmq;

/**
 * Represents the messages sent between Verticles inside the VehicleService.
 *
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public interface Messages {


    interface VehicleService {
        String BOOT = "vehicle.service.boot";

    }

    interface NearestControl {
        String AVAILABLE_REQUEST = "control.available";
        String DATA_TO_VEHICLE = "control.data.to.vehicle";
        String GET_VEHICLE = "election.vehicle.request";
    }

    interface VehicleFinder {
        String VEHICLE_RESPONSE = "finder.vehicle.response";
    }

    interface VehicleStore {
        String AVAILABLE_COMPLETED = "store.available.completed";
        String GET_VEHICLE_COMPLETED = "store.get.vehicle.completed";
        String REGISTER_VEHICLE_COMPLETED = "vehicle.register.completed";
    }

    interface VehicleRegister {
        String REGISTER_VEHICLE_REQUEST = "vehicle.register.request";
    }

}
