package com.wedriveu.services.vehicle.rabbitmq;

/**
 * Represents the message addresses used between Verticles inside the VehicleService.
 *
 * @author Marco Baldassarri on 30/07/2017.
 */
public interface Messages {

    /**
     * Key used to represent a sender of a message.
     */
    String SENDER_ID = "senderId";

    /**
     * Address used at the application startup from the Main class to start the
     * {@linkplain com.wedriveu.services.vehicle.app.BootVerticle} Verticle.
     */
    interface VehicleService {
        String BOOT = "vehicle.service.boot";
        String BOOT_COMPLETED = "vehicle.service.boot.completed";
        String BIND_EXCHANGE = "service.bind.exchange";
        String EXCHANGE_BINDED = "exchange.binded";
    }

    /**
     * Addresses used by the {@linkplain NearestControl} Verticle to handle the requests used
     * at the beginning of vehicle election process.
     */
    interface NearestControl {
        String AVAILABLE_REQUEST = "control.available";
        String DATA_TO_VEHICLE = "control.data.to.vehicle.%s";
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
        String GET_VEHICLE_COMPLETED_BOOKING = "store.get.vehicle.completed.for.booking.%s";
        String GET_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED =
                "store.get.vehicle.completed.for.nearest.substitution";
        String REGISTER_VEHICLE_COMPLETED = "vehicle.register.completed";
        String CLEAR_VEHICLES = "vehicle.clear";
        String CLEAR_VEHICLES_COMPLETED = "vehicle.clear.completed";
        String GET_VEHICLE_LIST_COMPLETED = "store.get.vehicle.list.completed";
        String UPDATE_VEHICLE_STATUS = "service.updates.eventbus.address";
        String GET_VEHICLE_FOR_SUBSTITUTION_COMPLETED = "store.get.vehicle.for.substitution.completed.%s";
        String GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION_COMPLETED =
                "store.get.vehicle.available.for.substitution.completed";
    }

    /**
     * Address used by the {@linkplain VehicleRegister} Verticle to receive new vehicle register requests from the
     * vehicle itself.
     */
    interface VehicleRegister {
        String REGISTER_VEHICLE_REQUEST = "vehicle.register.request";
    }

    /**
     * Addresses used for the vehicle substitution.
     */
    interface VehicleSubstitution {
        String START_SUBSTITUTION = "vehicle.substitution.start";
        String CHECK_FOR_SUBSTITUTION = "vehicle.substitution.check";
        String CHECK_FOR_SUBSTITUTION_COMPLETED = "vehicle.substitution.check.completed";
        String CHECK_FOR_SUBSTITUTION_FAILED = "vehicle.substitution.check.failed";
        String FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION = "vehicle.substitution.nearest.find.%s";
        String FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED = "vehicle.substitution.nearest.find.completed";
        String NO_VEHICLE_FOR_SUBSTITUTION = "vehicle.substitution.nearest.find.none";
        String GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION = "vehicle.substitution.get.available";
        String GET_VEHICLE_FOR_SUBSTITUTION = "vehicle.substitution.get";
        String GET_NEAREST_VEHICLE_FOR_SUBSTITUTION = "vehicle.substitution.nearest.get";
        String BOOK_FOR_SUBSTITUTION_RESPONSE = "vehicle.substitution.book.response";
        String SEND_SUBSTITUTION_VEHICLE_TO_USER = "vehicle.substitution.send.to.user";
        String UNDEPLOY = "vehicle.substitution.undeploy";
    }

    interface Analytics {
        String GET_VEHICLES_REQUEST = "analytics.get.vehicles.request";
    }

    interface Booking {
        String BOOK_REQUEST = "booking.book.request.to.vehicle.%s";
        String BOOK_RESPONSE = "booking.book.response.to.control";
        String START_VEHICLE = "booking.book.start.vehicle";
        String CHANGE_BOOKING_REQUEST = "booking.book.change.request";
        String CHANGE_BOOKING_RESPONSE = "booking.book.change.response";
        String GET_BOOKING_POSITIONS = "vehicle.booking.position";
        String GET_BOOKING_POSITIONS_COMPLETED = "vehicle.booking.position.completed.%s";
        String UNDEPLOY = "booking.book.vehicle.verticle.undeploy";

    }

    interface BookingControl {
        String PUBLISH_RESULT = "booking.control.publish.result.to.booking.service.and.analytics";
        String GET_VEHICLE_BOOKING = "booking.control.get.vehicle.from.licence.plate";
        String START_DRIVING = "booking.control.start.driving";
    }

    interface UpdateControl {
        String UPDATE_VEHICLE_STATUS = "vehicle.control.update.vehicle.status";
    }

    interface Trip {
        String DISTANCE_TO_USER = "distanceToUser";
        String TOTAL_DISTANCE = "totalDistance";
        String LICENSE_PLATE = "licensePlate";
        String SPEED = "speed";
    }

}
