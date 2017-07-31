package com.wedriveu.services.booking.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Michele Donati
 * @author Nicola Lasagni
 * This inteface models the <em>com.wedriveu.services.booking.entity.Booking' database store</em>.
 */
public interface BookingStore {

    /**
     * Adds a booking to the storeEntities.
     * @param booking The booking to be added to the storeEntities.
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean addBooking(Booking booking);

    /**
     *
     * @param bookingId Identifies the <em>com.wedriveu.services.booking.entity.Booking</em>'s <em>ID</em> that must be retreived.
     * @return Returns the {@linkplain Booking} with the provided id, only if found.
     */
    Optional<Booking> getBooking(int bookingId);

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
     * @return All the {@linkplain Booking}s inside the range of specified dates.
     */
    List<Booking> getBookingsByDate(Date fromDate, Date toDate);

    /**
     * Clears the {@linkplain BookingStore}.
     */
    void clear();

}
