package com.wedriveu.mobile.store;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.wedriveu.mobile.model.Booking;
import com.wedriveu.shared.util.Position;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * @author Nicola Lasagni on 29/08/2017.
 */
@RunWith(AndroidJUnit4.class)
public class BookingStoreTest {

    private static final String USERNAME = "DummyUser";
    private static final String ADDRESS = "Via Sacchi 3, Cesena";
    private static final Position USER_POSITION = new Position(10.0, 10.0);
    private static final Position DESTINATION_POSITION = new Position(11.0, 11.0);

    private BookingStore mBookingStore;

    @Before
    public void setUp() throws Exception {
        mBookingStore = new BookingStoreImpl(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void getBooking() throws Exception {
        assertNull(mBookingStore.getBooking());
    }

    @Test
    public void storeBooking() throws Exception {
        Booking booking = createBooking();
        mBookingStore.storeBooking(booking);
        Booking storedBooking = mBookingStore.getBooking();
        assertEquals(booking, storedBooking);
    }

    @Test
    public void clear() throws Exception {
        mBookingStore.clear();
        assertNull(mBookingStore.getBooking());
    }

    private Booking createBooking() {
        return new Booking(USERNAME, ADDRESS, USER_POSITION, DESTINATION_POSITION);
    }

}