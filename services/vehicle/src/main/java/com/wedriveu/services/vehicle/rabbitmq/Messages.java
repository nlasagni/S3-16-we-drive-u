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
     * Addresses used for general vehicle service purposes.
     */
    interface VehicleService {
        /**
         * Address used at the application startup from the {@link com.wedriveu.services.vehicle.app.Main}
         * class to start a boot operation.
         */
        String BOOT = "vehicle.service.boot";
        /**
         * Address used at the application startup from the {@link com.wedriveu.services.vehicle.app.BootVerticle}
         * class to communicate that the boot operation has completed.
         */
        String BOOT_COMPLETED = "vehicle.service.boot.completed";
        /**
         * Address used to communicate to an {@link ExchangeManagerVerticle} to declare an exchange.
         */
        String DECLARE_EXCHANGE = "service.bind.exchange";
        /**
         * Address used by the {@link ExchangeManagerVerticle} to communicate that it has declared the
         * specified exchange.
         */
        String EXCHANGE_DECLARED = "exchange.binded";
    }

    /**
     * Addresses used by the {@linkplain NearestControl} Verticle to handle the requests used
     * at the beginning of vehicle election process.
     */
    interface NearestControl {
        /**
         * The address used by a {@link com.wedriveu.services.vehicle.boundary.nearest.NearestConsumerVerticle}
         * boundary to communicate a {@link com.wedriveu.services.vehicle.entity.UserRequest} received.
         */
        String AVAILABLE_REQUEST = "control.available";
        /**
         * The address used by a {@link NearestControl} to communicate to a
         * {@link com.wedriveu.services.vehicle.boundary.nearest.VehicleFinderVerticle} to start
         * the find process.
         */
        String DATA_TO_VEHICLE = "control.data.to.vehicle.%s";
        /**
         * The address used by a {@link NearestControl} to communicate to a
         * {@link com.wedriveu.services.vehicle.entity.VehicleStoreImpl} to fetch the
         * {@link com.wedriveu.shared.util.Constants.Vehicle} elected.
         */
        String GET_VEHICLE_NEAREST = "election.vehicle.request";
    }

    /**
     * Address used by the {@linkplain VehicleFinder} Verticle to lookup vehicles that
     * can actually make the trip.
     */
    interface VehicleFinder {
        /**
         * The constant VEHICLE_RESPONSE.
         */
        String VEHICLE_RESPONSE = "finder.vehicle.response";
        /**
         * The constant VEHICLE_RESPONSE_RESULT.
         */
        String VEHICLE_RESPONSE_RESULT = "vehicleResponseResult";
        /**
         * The constant NO_VEHICLE.
         */
        String NO_VEHICLE = "finder.vehicle.none";
    }

    /**
     * Addresses used by the {@linkplain VehicleStore} Verticle to handle db requests and responses.
     */
    interface VehicleStore {
        /**
         * The constant AVAILABLE_COMPLETED.
         */
        String AVAILABLE_COMPLETED = "store.available.completed";
        /**
         * The constant GET_VEHICLE_COMPLETED_NEAREST.
         */
        String GET_VEHICLE_COMPLETED_NEAREST = "store.get.vehicle.completed.for.nearet";
        /**
         * The constant GET_VEHICLE_COMPLETED_BOOKING.
         */
        String GET_VEHICLE_COMPLETED_BOOKING = "store.get.vehicle.completed.for.booking.%s";
        /**
         * The constant GET_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED.
         */
        String GET_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED =
                "store.get.vehicle.completed.for.nearest.substitution";
        /**
         * The constant REGISTER_VEHICLE_COMPLETED.
         */
        String REGISTER_VEHICLE_COMPLETED = "vehicle.register.completed";
        /**
         * The constant CLEAR_VEHICLES.
         */
        String CLEAR_VEHICLES = "vehicle.clear";
        /**
         * The constant CLEAR_VEHICLES_COMPLETED.
         */
        String CLEAR_VEHICLES_COMPLETED = "vehicle.clear.completed";
        /**
         * The constant GET_VEHICLE_LIST_COMPLETED.
         */
        String GET_VEHICLE_LIST_COMPLETED = "store.get.vehicle.list.completed";
        /**
         * The constant UPDATE_VEHICLE_STATUS.
         */
        String UPDATE_VEHICLE_STATUS = "service.updates.eventbus.address";
        /**
         * The constant GET_VEHICLE_FOR_SUBSTITUTION_COMPLETED.
         */
        String GET_VEHICLE_FOR_SUBSTITUTION_COMPLETED = "store.get.vehicle.for.substitution.completed.%s";
        /**
         * The constant GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION_COMPLETED.
         */
        String GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION_COMPLETED =
                "store.get.vehicle.available.for.substitution.completed";
    }

    /**
     * Address used by the {@linkplain VehicleRegister} Verticle to receive new vehicle register requests
     * from a vehicle.
     */
    interface VehicleRegister {
        /**
         * The constant REGISTER_VEHICLE_REQUEST.
         */
        String REGISTER_VEHICLE_REQUEST = "vehicle.register.request";
    }

    /**
     * Addresses used for the vehicle substitution.
     */
    interface VehicleSubstitution {
        /**
         * The constant START_SUBSTITUTION.
         */
        String START_SUBSTITUTION = "vehicle.substitution.start";
        /**
         * The constant CHECK_FOR_SUBSTITUTION.
         */
        String CHECK_FOR_SUBSTITUTION = "vehicle.substitution.check.%s";
        /**
         * The constant CHECK_FOR_SUBSTITUTION_COMPLETED.
         */
        String CHECK_FOR_SUBSTITUTION_COMPLETED = "vehicle.substitution.check.completed";
        /**
         * The constant CHECK_FOR_SUBSTITUTION_FAILED.
         */
        String CHECK_FOR_SUBSTITUTION_FAILED = "vehicle.substitution.check.failed";
        /**
         * The constant FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION.
         */
        String FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION = "vehicle.substitution.nearest.find.%s";
        /**
         * The constant FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED.
         */
        String FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED = "vehicle.substitution.nearest.find.completed";
        /**
         * The constant NO_VEHICLE_FOR_SUBSTITUTION.
         */
        String NO_VEHICLE_FOR_SUBSTITUTION = "vehicle.substitution.nearest.find.none";
        /**
         * The constant GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION.
         */
        String GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION = "vehicle.substitution.get.available";
        /**
         * The constant GET_VEHICLE_FOR_SUBSTITUTION.
         */
        String GET_VEHICLE_FOR_SUBSTITUTION = "vehicle.substitution.get";
        /**
         * The constant GET_NEAREST_VEHICLE_FOR_SUBSTITUTION.
         */
        String GET_NEAREST_VEHICLE_FOR_SUBSTITUTION = "vehicle.substitution.nearest.get";
        /**
         * The constant BOOK_FOR_SUBSTITUTION_RESPONSE.
         */
        String BOOK_FOR_SUBSTITUTION_RESPONSE = "vehicle.substitution.book.response";
        /**
         * The constant SEND_SUBSTITUTION_VEHICLE_TO_USER.
         */
        String SEND_SUBSTITUTION_VEHICLE_TO_USER = "vehicle.substitution.send.to.user";
        /**
         * The constant UNDEPLOY.
         */
        String UNDEPLOY = "vehicle.substitution.undeploy";
    }

    /**
     * Addresses used for analytics purposes.
     */
    interface Analytics {
        /**
         * The address used to manage analytics service vehicles requests.
         */
        String GET_VEHICLES_REQUEST = "analytics.get.vehicles.request";
    }

    /**
     * Addresses used for booking purposes.
     */
    interface Booking {
        /**
         * The constant BOOK_REQUEST.
         */
        String BOOK_REQUEST = "booking.book.request.to.vehicle.%s";
        /**
         * The constant BOOK_RESPONSE.
         */
        String BOOK_RESPONSE = "booking.book.response.to.control";
        /**
         * The constant START_VEHICLE.
         */
        String START_VEHICLE = "booking.book.start.vehicle";
        /**
         * The constant CHANGE_BOOKING_REQUEST.
         */
        String CHANGE_BOOKING_REQUEST = "booking.book.change.request";
        /**
         * The constant CHANGE_BOOKING_RESPONSE.
         */
        String CHANGE_BOOKING_RESPONSE = "booking.book.change.response";
        /**
         * The constant GET_BOOKING_POSITIONS.
         */
        String GET_BOOKING_POSITIONS = "vehicle.booking.position";
        /**
         * The constant GET_BOOKING_POSITIONS_COMPLETED.
         */
        String GET_BOOKING_POSITIONS_COMPLETED = "vehicle.booking.position.completed.%s";
        /**
         * The constant UNDEPLOY.
         */
        String ABORT_BOOKING = "booking.book.abort";
        /**
         * The constant UNDEPLOY.
         */
        String UNDEPLOY = "booking.book.vehicle.verticle.undeploy";

    }

    /**
     * Addresses used directly by the {@link BookingControl}.
     */
    interface BookingControl {
        /**
         * The constant PUBLISH_RESULT.
         */
        String PUBLISH_RESULT = "booking.control.publish.result.to.booking.service.and.analytics";
        /**
         * The constant GET_VEHICLE_BOOKING.
         */
        String GET_VEHICLE_BOOKING = "booking.control.get.vehicle.from.licence.plate";
        /**
         * The constant START_DRIVING.
         */
        String START_DRIVING = "booking.control.start.driving";
    }

    /**
     * Addresses used directly by the {@link UpdateControl}.
     */
    interface UpdateControl {
        /**
         * The address used to communicate a status update of a vehicle.
         */
        String UPDATE_VEHICLE_STATUS = "vehicle.control.update.vehicle.status";
    }

    /**
     * Keys used for exchanging messages that involves vehicle trip computations.
     */
    interface Trip {
        /**
         * The key used put/get the distance from a vehicle to a user into a message.
         */
        String DISTANCE_TO_USER = "distanceToUser";
        /**
         * The key used put/get the total distance of a trip into a message.
         */
        String TOTAL_DISTANCE = "totalDistance";
        /**
         * The key used put/get the vehicle license plate into a message.
         */
        String LICENSE_PLATE = "licensePlate";
        /**
         * The key used put/get the vehicle speed into a message.
         */
        String SPEED = "vehicleSpeed";
    }

}
