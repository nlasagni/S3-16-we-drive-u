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
 * @author Michele on 22/07/2017.
 * @author Nicola Lasagni on 31/07/2017
 * @author Marco Baldassarri on 1/08/2017
 */
public interface Constants {

    String REGISTER_RESULT = "vehicleRegisterResult";

    //Vehicle Service Queues
    String VEHICLE_SERVICE_QUEUE_NEAREST = "service.vehicle.queue.nearest";
    String VEHICLE_SERVICE_QUEUE_FINDER = "service.vehicle.queue.finder.%s";
    String VEHICLE_SERVICE_QUEUE_ANALYTICS = "service.vehicle.queue.analytics";
    String VEHICLE_SERVICE_QUEUE_REGISTER = "service.vehicle.queue.register";
    String VEHICLE_SERVICE_QUEUE_BOOK_VEHICLE = "service.vehicle.queue.book.vehicle.%s";
    String VEHICLE_SERVICE_QUEUE_BOOK = "service.vehicle.queue.book";
    String VEHICLE_SERVICE_QUEUE_CHANGE_BOOKING = "service.vehicle.queue.change";
    String VEHICLE_SERVICE_QUEUE_FIND_BOOKING_POSITIONS = "service.vehicle.queue.find.booking.positions";
    String VEHICLE_SERVICE_QUEUE_VEHICLE_ARRIVED = "service.vehicle.queue.vehicle.arrived";
    String NEAREST_EVENT_BUS_ADDRESS = NearestConsumerVerticle.class.getCanonicalName();
    String EVENT_BUS_FINDER_ADDRESS = VehicleFinderVerticle.class.getCanonicalName();
    String EVENT_BUS_REGISTER_ADDRESS = RegisterConsumerVerticle.class.getCanonicalName();
    String EVENT_BUS_ANALYTICS_ADDRESS = AnalyticsConsumerVerticle.class.getCanonicalName();
    String EVENT_BUS_BOOK_ADDRESS = BookConsumerVerticle.class.getCanonicalName();
    String EVENT_BUS_CHANGE_BOOKING_ADDRESS = ChangeBookingConsumerVerticle.class.getCanonicalName();
    String EVENT_BUS_FIND_BOOKING_POSITION_ADDRESS = GetBookingPositionsVerticle.class.getCanonicalName();
    String EVENT_BUS_BOOK_VEHICLE_ADDRESS = BookVehicleVerticle.class.getCanonicalName();
}
