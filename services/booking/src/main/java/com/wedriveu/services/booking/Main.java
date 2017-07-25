package com.wedriveu.services.booking;

import com.wedriveu.services.booking.entity.Booking;
import com.wedriveu.services.booking.entity.BookingStoreImpl;

public class Main {

    public static void main(String[] args) {
        BookingStoreImpl obj = new BookingStoreImpl();
        obj.mapEntityToJson();
        Booking me;
        me = obj.getBooking(1);
    }

}
