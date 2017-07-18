package entity; /**
 * Created by Michele on 12/07/2017.
 */

/**
 * @author Michele Donati
 * This inteface models the <em>entity.Booking' database domain</em>.
 */
public interface BookingStore {

    /**
     * Maps a <em>entity.Booking</em> object in a JSon object.
     */
    public void mapEntityToJson();

    /**
     *
     * @param bookingId Identifies the <em>entity.Booking</em>'s <em>ID</em> that must be retreived.
     * @return Returns the <em>entity.Booking</em>, only if founded.
     */
    public Booking getBooking(int bookingId);

}
