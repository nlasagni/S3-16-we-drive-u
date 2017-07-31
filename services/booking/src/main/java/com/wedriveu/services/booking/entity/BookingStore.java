package com.wedriveu.services.booking.entity; /**
 * Created by Michele on 12/07/2017.
 */

import java.util.Date;
import java.util.List;

/**
 * @author Michele Donati
 * @author Nicola Lasagni
 * This inteface models the <em>com.wedriveu.services.booking.entity.Booking' database domain</em>.
 */
public interface BookingStore {

    /**
     * Maps a <em>com.wedriveu.services.booking.entity.Booking</em> object in a JSon object.
     */
    void mapEntityToJson();

    /**
     * Adds a booking to the store.
     * @param booking The booking to be added to the store.
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean addBooking(Booking booking);

    /**
     *
     * @param bookingId Identifies the <em>com.wedriveu.services.booking.entity.Booking</em>'s <em>ID</em> that must be retreived.
     * @return Returns the <em>com.wedriveu.services.booking.entity.Booking</em>, only if founded.
     */
    Booking getBooking(int bookingId);

    /**
     * Updates the status of a {@linkplain Booking}.
     *
     * @param bookingId The id of the {@linkplain Booking} to be updated.
     * @param bookingStatus The new status of the {@linkplain Booking}.
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean updateBookingStatus(int bookingId, String bookingStatus);

    /**
     * Fetches all {@linkplain Booking} stored that are inside the range of specified dates.
     *
     * @param fromDate Starting date of the range.
     * @param toDate End date of the range.
     * @return The {@linkplain List<Booking>} inside the range of specified dates.
     */
    List<Booking> getBookingsByDate(Date fromDate, Date toDate);

}
