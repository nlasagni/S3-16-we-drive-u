package entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Michele on 12/07/2017.
 */
public class Booking {

    private int bookingID;
    private Date date;
    private String username;
    private String carLicencePlate;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private Double sourceLatitude;
    private Double sourceLongitude;
    private String bookingState;

    public Booking(@JsonProperty("bookingID") int bookingID,
                   @JsonProperty("date")Date date,
                   @JsonProperty("username")String username,
                   @JsonProperty("carLicencePlate")String carLicencePlate,
                   @JsonProperty("destinationLatitude")Double destinationLatitude,
                   @JsonProperty("destinationLongitude") Double destinationLongitude,
                   @JsonProperty("sourceLatitude")Double sourceLatitude,
                   @JsonProperty("sourceLongitude")Double sourceLongitude,
                   @JsonProperty("bookingState") String bookingState) {
        this.bookingID = bookingID;
        this.date = date;
        this.username = username;
        this.carLicencePlate = carLicencePlate;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.sourceLatitude = sourceLatitude;
        this.sourceLongitude = sourceLongitude;
        this.bookingState = bookingState;
    }

    public int getBookingID() {
        return bookingID;
    }

    public Date getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public String getCarLicencePlate() {
        return carLicencePlate;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    public Double getSourceLongitude() {
        return sourceLongitude;
    }

    public String getBookingState() {
        return bookingState;
    }

    public void setBookingState(String bookingState) {
        this.bookingState = bookingState;
    }

}
