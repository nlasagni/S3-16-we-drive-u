import entity.Booking;
import entity.BookingStoreImpl;

public class Main {

    public static void main(String[] args) {
        BookingStoreImpl obj = new BookingStoreImpl();
        obj.mapEntityToJson();
        Booking me;
        me = obj.getBooking(1);
    }

}
