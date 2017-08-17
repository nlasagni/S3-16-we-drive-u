package com.wedriveu.services.vehicle.boundary.booking.entity;


import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;

/**
 * Simple factory pattern to get different Vehicle implementations.
 *
 * @author Marco Baldassarri on 02/08/2017.
 */
public interface BookingRequestFactory {

    BookVehicleRequest getBookingVehicleRequest();

}
