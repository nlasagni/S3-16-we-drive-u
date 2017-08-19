package com.wedriveu.mobile.store;

import com.wedriveu.mobile.model.Booking;

/**
 * The BookingStore that stores {@linkplain Booking} objects.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public interface BookingStore {

    /**
     * Gets a {@linkplain Booking} from the store.
     *
     * @return the booking
     */
    Booking getBooking();

    /**
     * Stores a {@linkplain Booking}.
     *
     * @param booking the booking
     */
    void storeBooking(Booking booking);

    /**
     * Clears the store content.
     */
    void clear();

}
