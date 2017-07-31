package com.wedriveu.services.booking.entity;

import com.wedriveu.services.shared.entity.EntityListStoreStrategy;
import com.wedriveu.services.shared.entity.JsonFileEntityListStoreStrategyImpl;
import com.wedriveu.services.shared.utilities.Constants;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

/**
 * @author Nicola Lasagni on 31/07/2017.
 */
public class BookingStoreTest {

    private static final int BOOKING_COUNT = 3;
    private static final String USERNAME = "username";
    private static final String LICENSE_PLATE = "licensePlate";

    private BookingStore bookingStore;
    private List<Booking> bookings;

    @Before
    public void setUp() throws Exception {
        EntityListStoreStrategy<Booking> storeStrategy =
                new JsonFileEntityListStoreStrategyImpl<>(Booking.class, Constants.BOOKINGS_DATABASE_PATH);
        bookingStore = new BookingStoreImpl(storeStrategy);
        bookings = createBookings();
    }

    @Test
    public void addBooking() throws Exception {
        Booking booking = bookings.get(0);
        boolean result = bookingStore.addBooking(booking);
        assertTrue(result && bookingStore.getBooking(booking.getBookingID()) != null);
    }

    @Test
    public void getBooking() throws Exception {
        Booking booking = bookings.get(0);
        boolean result = bookingStore.addBooking(booking);
        Booking storedBooking = bookingStore.getBooking(booking.getBookingID());
        assertTrue(result &&
                storedBooking != null &&
                storedBooking.getBookingID() == booking.getBookingID());
    }

    @Test
    public void updateBookingStatus() throws Exception {
        Booking booking = bookings.get(0);
        bookingStore.addBooking(booking);
        boolean updateResult = bookingStore.updateBookingStatus(booking.getBookingID(), Booking.STATUS_COMPLETED);
        Booking storedBooking = bookingStore.getBooking(booking.getBookingID());
        assertTrue(updateResult &&
                storedBooking != null &&
                storedBooking.getBookingState().equals(Booking.STATUS_COMPLETED));
    }

    @Test
    public void getBookingsByDate() throws Exception {
        Date date = new Date();
        bookings.forEach(booking -> bookingStore.addBooking(booking));
        List<Booking> storedBookings = bookingStore.getBookingsByDate(bookings.get(0).getDate(), date);
        assertTrue(storedBookings != null && storedBookings.size() == BOOKING_COUNT);
    }

    @Test
    public void clear() throws Exception {
        bookingStore.clear();
        assertTrue(bookingStore.getBooking(0) == null);
    }

    private List<Booking> createBookings() {
        List<Booking> bookings = new ArrayList<>();
        IntStream.range(0, BOOKING_COUNT).forEach(id -> {
            bookings.add(createBooking(id, id % 2 == 0 ? Booking.STATUS_STARTED : Booking.STATUS_PROCESSING));
        });
        return bookings;
    }

    private Booking createBooking(int id, String status) {
        return new Booking(id,
                new Date(),
                USERNAME + id,
                LICENSE_PLATE + id,
                0.0,
                0.0,
                0.0,
                0.0,
                status);
    }

}