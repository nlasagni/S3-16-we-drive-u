package com.wedriveu.services.vehicle.rabbitmq;

/**
 * Represents the message addresses used between Verticles inside the VehicleService.
 *
 * @author Marco Baldassarri on 30/07/2017.
 */
public interface Messages {

    /**
     * Address used at the application startup from the Main class to start the
     * {@linkplain com.wedriveu.services.vehicle.app.BootVerticle} Verticle.
     */
    interface VehicleService {
        String BOOT = "vehicle.service.boot";
        String BOOT_COMPLETED = "vehicle.service.boot.completed";

    }

    /**
     * Addresses used by the {@linkplain NearestControl} Verticle to handle the requests used
     * at the beginning of vehicle election process.
     */
    interface NearestControl {
        String AVAILABLE_REQUEST = "control.available";
        String DATA_TO_VEHICLE = "control.data.to.vehicle";
        String GET_VEHICLE_NEAREST = "election.vehicle.request";
    }

    /**
     * Address used by the {@linkplain VehicleFinder} Verticle to lookup vehicles that can actually make the trip.
     */
    interface VehicleFinder {
        String VEHICLE_RESPONSE = "finder.vehicle.response";
        String VEHICLE_RESPONSE_RESULT = "vehicleResponseResult";
        String NO_VEHICLE = "finder.vehicle.none";
    }

    /**
     * Addresses used by the {@linkplain VehicleStore} Verticle to handle db requests and responses.
     */
    interface VehicleStore {
        String AVAILABLE_COMPLETED = "store.available.completed";
        String GET_VEHICLE_COMPLETED_NEAREST = "store.get.vehicle.completed.for.nearet";
        String GET_VEHICLE_COMPLETED_BOOKING = "store.get.vehicle.completed.for.booking";
        String REGISTER_VEHICLE_COMPLETED = "vehicle.register.completed";
        String CLEAR_VEHICLES = "vehicle.clear";
        String CLEAR_VEHICLES_COMPLETED = "vehicle.clear.completed";
        String GET_VEHICLE_LIST_COMPLETED = "store.get.vehicle.list.completed";
        String UPDATE_VEHICLE_STATUS = "service.updates.eventbus.address";
    }

    /**
     * Address used by the {@linkplain VehicleRegister} Verticle to receive new vehicle register requests from the
     * vehicle itself.
     */
    interface VehicleRegister {
        String REGISTER_VEHICLE_REQUEST = "vehicle.register.request";
    }

    interface Analytics {
        String GET_VEHICLES_REQUEST = "analytics.get.vehicles.request";
    }

    interface Booking {
        String BOOK_REQUEST = "booking.book.request.to.vehicle";
        String BOOK_RESPONSE = "booking.book.response.to.control";
        String UNDEPLOY = "booking.book.vehicle.verticle.undeploy";

    }

    interface BookingControl {
        String PUBLISH_RESULT = "booking.control.publish.result.to.booking.service.and.analytics";

        String GET_VEHICLE_BOOKING = "booking.control.get.vehicle.from.licence.plate";
        String START_DRIVING = "booking.control.start.driving";
    }
}
