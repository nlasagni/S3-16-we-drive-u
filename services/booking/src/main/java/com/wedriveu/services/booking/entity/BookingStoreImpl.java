package com.wedriveu.services.booking.entity;

import com.wedriveu.services.shared.entity.EntityStoreStrategy;
import com.wedriveu.services.shared.entity.JsonFileStoreStrategyImpl;
import com.wedriveu.services.shared.utilities.Constants;

import java.util.Date;
import java.util.List;

/**
 * Created by Michele on 12/07/2017.
 */
public class BookingStoreImpl implements BookingStore {

    private EntityStoreStrategy<List<Booking>> storeStrategy;

    public BookingStoreImpl() {
        this.storeStrategy = new JsonFileStoreStrategyImpl<>(Constants.BOOKINGS_DATABASE_PATH);
    }

    @Override
    public boolean addBooking(Booking booking) {
        return false;
    }

    @Override
    public Booking getBooking(int bookingId) {
        return null;
    }

    @Override
    public boolean updateBookingStatus(int bookingId, String bookingStatus) {
        return false;
    }

    @Override
    public List<Booking> getBookingsByDate(Date fromDate, Date toDate) {
        return null;
    }
    
}
