/**
 * Created by Michele on 12/07/2017.
 */

/**
 * @author Michele Donati
 * This inteface models the <em>Booking' database domain</em>.
 */
public interface BookingStore {

    /**
     * Maps a <em>Booking</em> object in a JSon object.
     */
    public void mapEntityToJson();

    /**
     *
     * @param bookingId Identifies the <em>Booking</em>'s <em>ID</em> that must be retreived.
     * @return Returns the <em>Booking</em>, only if founded.
     */
    public Booking getBooking(int bookingId);

}
