public class Main {

    public static void main(String[] args) {
        BookingStoreImpl obj = new BookingStoreImpl();
        obj.mapBookingToJSon();
        Booking me;
        me = obj.getBooking(1);
    }
}
