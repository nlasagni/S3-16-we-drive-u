package com.wedriveu.services.booking.entity;

import com.wedriveu.services.shared.model.Booking;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * A store that manages {@linkplain Booking}s.
 *
 * @author Michele Donati
 * @author Nicola Lasagni
 * This inteface models the <em>Booking' database store</em>.
 */
public interface BookingStore {

    /**
     * Generates a unique id.
     *
     * @return A new unique generated id.
     */
    int generateId();

    /**
     * Adds a booking to the storeEntities.
     *
     * @param booking The booking to be added to the storeEntities.
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean addBooking(Booking booking);

    /**
     * Gets a {@linkplain Booking} with the provided {@code bookingId}
     * @param bookingId Identifies the <em>Booking</em>'s <em>ID</em> that must be retreived.
     * @return Returns the {@linkplain Booking} with the provided id, only if found.
     */
    Optional<Booking> getBookingById(int bookingId);

    /**
     *
     * @param licensePlate Identifies the vehicle associated to the <em>Booking</em>'s <em>ID</em> that must be retreived.
     * @return Returns the started {@linkplain Booking} with the provided license plate, only if found.
     */
    Optional<Booking> getStartedBookingByLicensePlate(String licensePlate);

    /**
     * Gets the {@linkplain Booking} started by a {@linkplain com.wedriveu.services.shared.model.User}
     *
     * @param username Identifies username to which the booking is associated.
     * @return Returns the started {@linkplain Booking} associated to the provided username, only if found.
     */
    Optional<Booking> getUserStartedBooking(String username);

    /**
     * Updates the status of a {@linkplain Booking}.
     *
     * @param bookingId     The id of the {@linkplain Booking} to be updated.
     * @param bookingStatus The new status of the {@linkplain Booking}.
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean updateBookingStatus(int bookingId, String bookingStatus);

    /**
     * Fetches all {@linkplain Booking} stored that are inside the range of specified dates.
     *
     * @param fromDate Starting date of the range.
     * @param toDate   End date of the range.
     * @return All the {@linkplain Booking}s inside the range of specified dates.
     */
    List<Booking> getBookingsByDate(Date fromDate, Date toDate);

    /** Deletes a {@linkplain Booking} from the store.
     *
     * @param id The id of the booking to be deleted.
     * @return A boolean indicating the success or the failure of this operation.
     */
    boolean deleteBooking(int id);

    /**
     * Clears the {@linkplain BookingStore}.
     */
    void deleteAllBookings();

}
