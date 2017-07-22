package com.wedriveu.services.booking.entity; /**
 * Created by Michele on 12/07/2017.
 */

/**
 * @author Michele Donati
 * This inteface models the <em>com.wedriveu.services.booking.entity.Booking' database domain</em>.
 */
public interface BookingStore {

    /**
     * Maps a <em>com.wedriveu.services.booking.entity.Booking</em> object in a JSon object.
     */
    void mapEntityToJson();

    /**
     *
     * @param bookingId Identifies the <em>com.wedriveu.services.booking.entity.Booking</em>'s <em>ID</em> that must be retreived.
     * @return Returns the <em>com.wedriveu.services.booking.entity.Booking</em>, only if founded.
     */
    Booking getBooking(int bookingId);

}
