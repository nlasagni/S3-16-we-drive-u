package com.wedriveu.mobile.service.booking;

import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.shared.rabbitmq.message.CreateBookingResponse;

/**
 * Service that provides the capability to accept a booking.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public interface BookingService {

    /**
     * Accepts a booking.
     *
     * @param <T>      the type of the handler of the service result
     * @param username the username
     * @param booking  the booking
     * @param handler  the handler
     */
    <T> void acceptBooking(String username,
                           Booking booking,
                           ServiceOperationHandler<T, CreateBookingResponse> handler);

}
