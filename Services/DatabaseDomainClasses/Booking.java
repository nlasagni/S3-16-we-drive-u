package DatabaseDomainClasses;

import java.util.Date;

/**
 * Created by unibo on 07/07/2017.
 */
public class Booking {
    private int bookingID;
    private Date date;
    private String username;
    private String carLicencePlate;
    private Double latitudeDestination;
    private Double longitudeDestination;
    private Double latitudeSource;
    private Double longitudeSource;
    private String bookingState; //to define

    public Booking(int bookingID, java.util.Date date, String username, String carLicencePlate, Double latitudeDestination, Double longitudeDestination, Double latitudeSource, Double longitudeSource, String bookingState) {
        this.bookingID = bookingID;
        this.date = date;
        this.username = username;
        this.carLicencePlate = carLicencePlate;
        this.latitudeDestination = latitudeDestination;
        this.longitudeDestination = longitudeDestination;
        this.latitudeSource = latitudeSource;
        this.longitudeSource = longitudeSource;
        this.bookingState = bookingState;
    }


    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCarLicencePlate() {
        return carLicencePlate;
    }

    public void setCarLicencePlate(String carLicencePlate) {
        this.carLicencePlate = carLicencePlate;
    }

    public Double getLatitudeDestination() {
        return latitudeDestination;
    }

    public void setLatitudeDestination(Double latitudeDestination) {
        this.latitudeDestination = latitudeDestination;
    }

    public Double getLongitudeDestination() {
        return longitudeDestination;
    }

    public void setLongitudeDestination(Double longitudeDestination) {
        this.longitudeDestination = longitudeDestination;
    }

    public Double getLatitudeSource() {
        return latitudeSource;
    }

    public void setLatitudeSource(Double latitudeSource) {
        this.latitudeSource = latitudeSource;
    }

    public Double getLongitudeSource() {
        return longitudeSource;
    }

    public void setLongitudeSource(Double longitudeSource) {
        this.longitudeSource = longitudeSource;
    }

    public String getBookingState() {
        return bookingState;
    }

    public void setBookingState(String bookingState) {
        this.bookingState = bookingState;
    }
}
