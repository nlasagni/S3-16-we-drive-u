package com.wedriveu.backoffice.booking;

import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.shared.util.Position;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
public class BookingListGenerator {
    public List<Booking> getBookingsFromBookingService() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(
                new Booking(1,
                    new Date(2017,11,10),
                        "pippo",
                        "veicolo1",
                        new Position(44,12),
                        new Position(44.9,12),
                        Booking.STATUS_COMPLETED));
        bookings.add(
                new Booking(2,
                        new Date(2017,11,11),
                        "pippo",
                        "veicolo1",
                        new Position(44,12),
                        new Position(44.9,12),
                        Booking.STATUS_COMPLETED));
        bookings.add(
                new Booking(3,
                        new Date(2017,11,12),
                        "pippo",
                        "veicolo1",
                        new Position(44,12),
                        new Position(44.9,12),
                        Booking.STATUS_COMPLETED));
        return bookings;
    }

}
