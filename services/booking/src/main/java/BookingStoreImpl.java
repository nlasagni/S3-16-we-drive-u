import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.print.Book;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Michele on 12/07/2017.
 */
public class BookingStoreImpl implements BookingStore {

    @Override
    public void mapBookingToJSon() {
        ObjectMapper mapper = new ObjectMapper();

        Booking booking1 = createDummyObject(1, new Date(2017, 12, 29, 11, 23, 34), "Nicola", "MACCHINA1", 20.0,12.1, 30.0,31.1, false);
        Booking booking2 = createDummyObject(2, new Date(2017, 11, 28, 10, 22, 33), "Marco", "MACCHINA2", 19.9,11.0, 29.9,30.0, false);
        Booking booking3 = createDummyObject(3, new Date(2017, 10, 27, 9, 21, 32), "Stefano", "MACCHINA3", 18.8,10.9, 28.8,29.9, true);
        Booking booking4 = createDummyObject(4, new Date(2017, 9, 26, 8, 20, 31), "Michele", "MACCHINA4", 17.7,9.8, 27.7,28.8, true);

        ArrayList<Booking> bookingListToJSon = new ArrayList<Booking>();
        bookingListToJSon.add(booking1);
        bookingListToJSon.add(booking2);
        bookingListToJSon.add(booking3);
        bookingListToJSon.add(booking4);


        try {
            // Convert object to JSON string and save into a file directly
            mapper.writeValue(new File("D:\\bookings.json"), bookingListToJSon);

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(bookingListToJSon);
            System.out.println(jsonInString);

            // Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookingListToJSon);
            System.out.println(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Booking getBooking(int bookingID) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convert JSON string from file to Object
            Booking[] bookingListFromJSon= new Booking[10];
            bookingListFromJSon = mapper.readValue(new File("D:\\bookings.json"), Booking[].class);
            for (int i =0; i < bookingListFromJSon.length; i++) {
                Booking actualBooking = bookingListFromJSon[i];
                if(actualBooking.getBookingID() == bookingID) {
                    System.out.println("Booking found! -> User: " + actualBooking.getUsername() + " Vehicle: " + actualBooking.getCarLicencePlate() + " done in date: " + actualBooking.getDate().toString());
                    return actualBooking;
                }
            }
            System.out.println("Booking not found, retry!");
            return null;

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Booking createDummyObject(int bookingID, Date date, String username, String carLicencePlate, Double latitudeDestination, Double longitudeDestination, Double latitudeSource, Double longitudeSource, Boolean isBookingCompleted){
        Booking booking = new Booking(bookingID, date, username, carLicencePlate, latitudeDestination, longitudeDestination, latitudeSource, longitudeSource, isBookingCompleted);
        return booking;
    }

}
