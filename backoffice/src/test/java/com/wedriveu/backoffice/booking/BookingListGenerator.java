package com.wedriveu.backoffice.booking;

import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.shared.util.Position;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
public class BookingListGenerator {
    private static int YEAR = 2017;
    private static int MONTH = 11;
    private static int DAY1 = 21;
    private static String USERNAME = "pippo";
    private static String LICENSE_PLATE = "veicolo1";
    private static Position POSITION_START = new Position(44,12);
    private static Position POSITION_END = new Position(44.9,12);


    /**
     * gets a static booking list for testing the booking retriever class
     *
     * @return a list of bookings always equal
     */

    public static List<Booking> getBookingsFromBookingService() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(
                new Booking(1,
                    new Date(YEAR,MONTH, DAY1),
                        USERNAME,
                        LICENSE_PLATE,
                        POSITION_START,
                        POSITION_END,
                        Booking.STATUS_COMPLETED));
        bookings.add(
                new Booking(2,
                        new Date(YEAR,MONTH, DAY1+1),
                        USERNAME,
                        LICENSE_PLATE,
                        POSITION_START,
                        POSITION_END,
                        Booking.STATUS_COMPLETED));
        bookings.add(
                new Booking(3,
                        new Date(YEAR,MONTH,DAY1+2),
                        USERNAME,
                        LICENSE_PLATE,
                        POSITION_START,
                        POSITION_END,
                        Booking.STATUS_COMPLETED));
        return bookings;
    }

}
