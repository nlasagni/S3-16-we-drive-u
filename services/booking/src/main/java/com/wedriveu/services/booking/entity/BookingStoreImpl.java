package com.wedriveu.services.booking.entity;

import com.wedriveu.services.shared.entity.Booking;
import com.wedriveu.services.shared.entity.EntityListStoreStrategy;
import com.wedriveu.services.shared.utilities.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Michele on 12/07/2017.
 */
public class BookingStoreImpl implements BookingStore {

    private static final String TAG = BookingStoreImpl.class.getSimpleName();
    private static final String ADD_ERROR = "Error while adding booking";
    private static final String GET_ERROR = "Error while getting booking";
    private static final String GET_BY_DATE_ERROR = "Error while getting bookings by date";
    private static final String UPDATE_ERROR = "Error while updating booking";
    private static final String CLEAR_ERROR = "Error while clearing store";

    private EntityListStoreStrategy<Booking> storeStrategy;

    public BookingStoreImpl(EntityListStoreStrategy<Booking> storeStrategy) {
        this.storeStrategy = storeStrategy;
    }

    @Override
    public boolean addBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            if (bookings == null) {
                bookings = new ArrayList<>();
            }
            bookings.add(booking);
            storeStrategy.storeEntities(bookings);
            return true;
        } catch (Exception e) {
            Log.error(TAG, ADD_ERROR, e);
        }
        return false;
    }

    @Override
    public Optional<Booking> getBooking(int bookingId) {
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            Optional<Booking> booking = Optional.empty();
            if (bookings != null) {
                booking = bookings.stream().filter(b -> b.getId() == bookingId).findFirst();
            }
            return booking;
        } catch (Exception e) {
            Log.error(TAG, GET_ERROR, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateBookingStatus(int bookingId, String bookingStatus) {
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            if (bookings != null && !bookings.isEmpty()) {
                IntStream.range(0, bookings.size()).forEach(i -> {
                    Booking booking = bookings.get(i);
                    if (booking.getId() == bookingId) {
                        booking.setBookingStatus(bookingStatus);
                    }
                });
            }
            storeStrategy.storeEntities(bookings);
            return true;
        } catch (Exception e) {
            Log.error(TAG, UPDATE_ERROR, e);
        }
        return false;
    }

    @Override
    public List<Booking> getBookingsByDate(Date fromDate, Date toDate) {
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            return bookings.stream()
                    .filter(b -> b.getDate().getTime() >= fromDate.getTime() && b.getDate().getTime() <= toDate.getTime())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.error(TAG, GET_BY_DATE_ERROR, e);
        }
        return new ArrayList<>();
    }

    @Override
    public void clear() {
        try {
            storeStrategy.clear();
        } catch (Exception e) {
            Log.error(TAG, CLEAR_ERROR, e);
        }
    }

}
