package com.wedriveu.services.booking.entity;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Michele on 12/07/2017.
 */
public class BookingStoreImpl implements BookingStore {

    @Override
    public void mapEntityToJson() {
        Booking booking1 = createDummyObject(1,
                 new Date(2017, 12, 29, 11, 23, 34),
                "Nicola",
                "MACCHINA1",
                20.0,
                12.1,
                30.0,
                31.1,
                "started");
        Booking booking2 = createDummyObject(2,
                new Date(2017, 11, 28, 10, 22, 33),
                "Marco",
                "MACCHINA2",
                19.9,
                11.0,
                29.9,
                30.0,
                "processing");
        Booking booking3 = createDummyObject(3,
                new Date(2017, 10, 27, 9, 21, 32),
                "Stefano",
                "MACCHINA3",
                18.8,
                10.9,
                28.8,
                29.9,
                "complete");
        Booking booking4 = createDummyObject(4,
                new Date(2017, 9, 26, 8, 20, 31),
                "Michele",
                "MACCHINA4",
                17.7,
                9.8,
                27.7,
                28.8,
                "complete");

        ArrayList<Booking> bookingListToJSon = new ArrayList<Booking>();
        bookingListToJSon.add(booking1);
        bookingListToJSon.add(booking2);
        bookingListToJSon.add(booking3);
        bookingListToJSon.add(booking4);

        writeJsonBookingsFile(bookingListToJSon);
    }

    @Override
    public Booking getBooking(int bookingId) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Booking> bookings =
                    mapper.readValue(new File(Constants.BOOKINGS_DATABASE_PATH), new TypeReference<List<Booking>>(){});
            return getBookingRequestedCheckingBookingsList(bookings, bookingId);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void writeJsonBookingsFile(ArrayList<Booking> bookingListToJSon) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new File(Constants.BOOKINGS_DATABASE_PATH), bookingListToJSon);
            String jsonInString = mapper.writeValueAsString(bookingListToJSon);
            Log.log(jsonInString);
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookingListToJSon);
            Log.log(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Booking getBookingRequestedCheckingBookingsList(List<Booking> bookings, int bookingId) {
        for (Booking booking : bookings) {
            if(booking.getBookingID() == bookingId) {
                Log.log("com.wedriveu.services.booking.entity.Booking found! -> User: " +
                        booking.getUsername() +
                        " Vehicle: " +
                        booking.getCarLicencePlate() +
                        " done in date: " +
                        booking.getDate().toString());

                return booking;
            }
        }
        Log.log("com.wedriveu.services.booking.entity.Booking not found, retry!");
        return null;
    }

    public Booking createDummyObject(int bookingID,
                                     Date date,
                                     String username,
                                     String carLicencePlate,
                                     Double latitudeDestination,
                                     Double longitudeDestination,
                                     Double latitudeSource,
                                     Double longitudeSource,
                                     String bookingState){
        return new Booking(bookingID,
                date,
                username,
                carLicencePlate,
                latitudeDestination,
                longitudeDestination,
                latitudeSource,
                longitudeSource,
                bookingState);
    }


}
