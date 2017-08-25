package com.wedriveu.services.booking.entity;

import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.services.shared.store.EntityListStoreStrategy;
import com.wedriveu.shared.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Michele on 12/07/2017.
 * @author Nicola Lasagni
 */
public class BookingStoreImpl implements BookingStore {

    private static final String TAG = BookingStoreImpl.class.getSimpleName();
    private static final String GENERATE_ID_ERROR = "Error while generating id";
    private static final String ADD_ERROR = "Error while adding booking";
    private static final String GET_ERROR = "Error while getting booking";
    private static final String GET_BOOKINGS_ERROR = "Error while getting all bookings";
    private static final String UPDATE_ERROR = "Error while updating booking";
    private static final String DELETE_ERROR = "Error while deleting booking";
    private static final String DELETE_ALL_ERROR = "Error while deleting all bookings";

    private EntityListStoreStrategy<Booking> storeStrategy;

    public BookingStoreImpl(EntityListStoreStrategy<Booking> storeStrategy) {
        this.storeStrategy = storeStrategy;
    }

    @Override
    public int generateId() {
        int id = -1;
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            if (bookings != null && !bookings.isEmpty()) {
                Optional<Booking> booking = bookings.stream().sorted((o1, o2) ->
                        Integer.valueOf(o2.getId()).compareTo(o1.getId())
                ).findFirst();
                if (booking.isPresent()) {
                    id = booking.get().getId() + 1;
                }
            } else {
                id = 1;
            }
        } catch (Exception e) {
            Log.error(TAG, GENERATE_ID_ERROR, e);
        }
        return id;
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
    public Optional<Booking> getBookingById(int bookingId) {
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
    public Optional<Booking> getStartedBookingByLicensePlate(String licensePlate) {
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            Optional<Booking> booking = Optional.empty();
            if (bookings != null) {
                booking = bookings.stream().filter(b ->
                        licensePlate.equals(b.getVehicleLicensePlate()) &&
                                Booking.STATUS_STARTED.equals(b.getBookingStatus())
                ).findFirst();
            }
            return booking;
        } catch (Exception e) {
            Log.error(TAG, GET_ERROR, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Booking> getBookingByUser(String username, String bookingStatus) {
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            Optional<Booking> booking = Optional.empty();
            if (bookings != null) {
                booking = bookings.stream().filter(b ->
                        username.equals(b.getUsername()) && bookingStatus.equals(b.getBookingStatus())
                ).findFirst();
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
    public boolean updateBookingLicensePlate(int bookingId, String licensePlate) {
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            if (bookings != null && !bookings.isEmpty()) {
                IntStream.range(0, bookings.size()).forEach(i -> {
                    Booking booking = bookings.get(i);
                    if (booking.getId() == bookingId) {
                        booking.setVehicleLicensePlate(licensePlate);
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
    public List<Booking> getBookings() {
        try {
            return storeStrategy.getEntities();
        } catch (Exception e) {
            Log.error(TAG, GET_BOOKINGS_ERROR, e);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean deleteBooking(int id) {
        try {
            List<Booking> bookings = storeStrategy.getEntities();
            List<Booking> updatedBookings = bookings.stream()
                    .filter(b -> b.getId() != id)
                    .collect(Collectors.toList());
            storeStrategy.clear();
            storeStrategy.storeEntities(updatedBookings);
            return true;
        } catch (Exception e) {
            Log.error(TAG, DELETE_ERROR, e);
        }
        return false;
    }

    @Override
    public void deleteAllBookings() {
        try {
            storeStrategy.clear();
        } catch (Exception e) {
            Log.error(TAG, DELETE_ALL_ERROR, e);
        }
    }

}
