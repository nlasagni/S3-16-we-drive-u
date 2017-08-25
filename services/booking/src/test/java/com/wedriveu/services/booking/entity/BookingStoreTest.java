package com.wedriveu.services.booking.entity;

import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.services.shared.store.EntityListStoreStrategy;
import com.wedriveu.services.shared.store.JsonFileEntityListStoreStrategyImpl;
import com.wedriveu.shared.util.Position;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

/**
 * @author Nicola Lasagni on 31/07/2017.
 */
public class BookingStoreTest {

    private static final int BOOKING_COUNT = 3;
    private static final String USERNAME = "username";
    private static final String LICENSE_PLATE = "licensePlate";
    private static final String DATABASE_FILE_NAME = BookingStoreTest.class.getSimpleName() + ".json";

    private BookingStore bookingStore;
    private List<Booking> bookings;

    @Before
    public void setUp() throws Exception {
        EntityListStoreStrategy<Booking> storeStrategy =
                new JsonFileEntityListStoreStrategyImpl<>(Booking.class, DATABASE_FILE_NAME);
        bookingStore = new BookingStoreImpl(storeStrategy);
        bookings = createBookings();
    }

    @After
    public void tearDown() throws Exception {
        bookingStore.deleteAllBookings();
    }

    @Test
    public void addBooking() throws Exception {
        Booking booking = bookings.get(0);
        boolean result = bookingStore.addBooking(booking);
        assertTrue(result && bookingStore.getBookingById(booking.getId()) != null);
    }

    @Test
    public void getBooking() throws Exception {
        Booking booking = bookings.get(0);
        boolean result = bookingStore.addBooking(booking);
        Optional<Booking> storedBooking = bookingStore.getBookingById(booking.getId());
        assertTrue(result &&
                storedBooking.isPresent() &&
                storedBooking.get().getId() == booking.getId());
    }

    @Test
    public void updateBookingStatus() throws Exception {
        Booking booking = bookings.get(0);
        bookingStore.addBooking(booking);
        boolean updateResult = bookingStore.updateBookingStatus(booking.getId(), Booking.STATUS_COMPLETED);
        Optional<Booking> storedBooking = bookingStore.getBookingById(booking.getId());
        assertTrue(updateResult &&
                storedBooking.isPresent() &&
                storedBooking.get().getBookingStatus().equals(Booking.STATUS_COMPLETED));
    }

    @Test
    public void getBookings() throws Exception {
        bookings.forEach(booking -> bookingStore.addBooking(booking));
        List<Booking> storedBookings = bookingStore.getBookings();
        assertTrue(storedBookings != null && storedBookings.size() == BOOKING_COUNT);
    }

    @Test
    public void clear() throws Exception {
        bookingStore.deleteAllBookings();
        assertTrue(!bookingStore.getBookingById(0).isPresent());
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
                new Position(0.0, 0.0),
                new Position(0.0, 0.0),
                status);
    }

}