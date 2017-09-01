package com.wedriveu.services.vehicle.rabbitmq;

import com.wedriveu.services.vehicle.boundary.analytics.AnalyticsConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.booking.BookConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.booking.BookVehicleVerticle;
import com.wedriveu.services.vehicle.boundary.booking.ChangeBookingConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.booking.GetBookingPositionsVerticle;
import com.wedriveu.services.vehicle.boundary.nearest.NearestConsumerVerticle;
import com.wedriveu.services.vehicle.boundary.nearest.VehicleFinderVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.RegisterConsumerVerticle;

/**
 * The interface Constants.
 *
 * @author Michele on 22/07/2017.
 * @author Nicola Lasagni on 31/07/2017
 * @author Marco Baldassarri on 1/08/2017
 */
public interface Constants {

    /**
     * The constant NO_ELIGIBLE_VEHICLE.
     */
    String NO_ELIGIBLE_VEHICLE = "No vehicles nearby, please try again later or change your address";
    /**
     * The constant NO_ELIGIBLE_VEHICLE_FOR_SUSTITUTION.
     */
    String NO_ELIGIBLE_VEHICLE_FOR_SUSTITUTION = "No vehicle for substitution, please contact the assistance service.";

    /**
     * The constant REGISTER_RESULT.
     */
    String REGISTER_RESULT = "vehicleRegisterResult";

    /**
     * The constant VEHICLE_SERVICE_QUEUE_NEAREST.
     */
    String VEHICLE_SERVICE_QUEUE_NEAREST = "service.vehicle.queue.nearest";
    /**
     * The constant VEHICLE_SERVICE_QUEUE_FINDER.
     */
    String VEHICLE_SERVICE_QUEUE_FINDER = "service.vehicle.queue.finder.%s";
    /**
     * The constant VEHICLE_SERVICE_QUEUE_ANALYTICS.
     */
    String VEHICLE_SERVICE_QUEUE_ANALYTICS = "service.vehicle.queue.analytics";
    /**
     * The constant VEHICLE_SERVICE_QUEUE_REGISTER.
     */
    String VEHICLE_SERVICE_QUEUE_REGISTER = "service.vehicle.queue.register";
    /**
     * The constant VEHICLE_SERVICE_QUEUE_BOOK_VEHICLE.
     */
    String VEHICLE_SERVICE_QUEUE_BOOK_VEHICLE = "service.vehicle.queue.book.vehicle.%s";
    /**
     * The constant VEHICLE_SERVICE_QUEUE_BOOK.
     */
    String VEHICLE_SERVICE_QUEUE_BOOK = "service.vehicle.queue.book";
    /**
     * The constant VEHICLE_SERVICE_QUEUE_CHANGE_BOOKING.
     */
    String VEHICLE_SERVICE_QUEUE_CHANGE_BOOKING = "service.vehicle.queue.change";
    /**
     * The constant VEHICLE_SERVICE_QUEUE_FIND_BOOKING_POSITIONS.
     */
    String VEHICLE_SERVICE_QUEUE_FIND_BOOKING_POSITIONS = "service.vehicle.queue.find.booking.positions";
    /**
     * The constant VEHICLE_SERVICE_QUEUE_VEHICLE_ARRIVED.
     */
    String VEHICLE_SERVICE_QUEUE_VEHICLE_ARRIVED = "service.vehicle.queue.vehicle.arrived";
    /**
     * The constant NEAREST_EVENT_BUS_ADDRESS.
     */
    String NEAREST_EVENT_BUS_ADDRESS = NearestConsumerVerticle.class.getCanonicalName();
    /**
     * The constant EVENT_BUS_FINDER_ADDRESS.
     */
    String EVENT_BUS_FINDER_ADDRESS = VehicleFinderVerticle.class.getCanonicalName();
    /**
     * The constant EVENT_BUS_REGISTER_ADDRESS.
     */
    String EVENT_BUS_REGISTER_ADDRESS = RegisterConsumerVerticle.class.getCanonicalName();
    /**
     * The constant EVENT_BUS_ANALYTICS_ADDRESS.
     */
    String EVENT_BUS_ANALYTICS_ADDRESS = AnalyticsConsumerVerticle.class.getCanonicalName();
    /**
     * The constant EVENT_BUS_BOOK_ADDRESS.
     */
    String EVENT_BUS_BOOK_ADDRESS = BookConsumerVerticle.class.getCanonicalName();
    /**
     * The constant EVENT_BUS_CHANGE_BOOKING_ADDRESS.
     */
    String EVENT_BUS_CHANGE_BOOKING_ADDRESS = ChangeBookingConsumerVerticle.class.getCanonicalName();
    /**
     * The constant EVENT_BUS_FIND_BOOKING_POSITION_ADDRESS.
     */
    String EVENT_BUS_FIND_BOOKING_POSITION_ADDRESS = GetBookingPositionsVerticle.class.getCanonicalName();
    /**
     * The constant EVENT_BUS_BOOK_VEHICLE_ADDRESS.
     */
    String EVENT_BUS_BOOK_VEHICLE_ADDRESS = BookVehicleVerticle.class.getCanonicalName();
}
