package com.wedriveu.services.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.util.Position;

import java.util.Date;

/**
 * Booking model class, which can be associated to a {@linkplain User} through its
 * username and to a {@linkplain Vehicle} through its license plate.
 *
 * @author Michele on 12/07/2017.
 * @author Nicola Lasagni on 31/07/2017
 */
public class Booking {

    /**
     * This status represents a {@linkplain Booking} that has been created
     * but not been processed yet by the system.
     */
    public static final String STATUS_STARTED = "started";
    /**
     * This status represents a {@linkplain Booking} that has been processed
     * by the system and so it means that a {@linkplain Vehicle} will arrive at the {@linkplain User} soon.
     */
    public static final String STATUS_PROCESSING = "processing";
    /**
     * This status represents a {@linkplain Booking} that has been completed,
     * so it means that a {@linkplain Vehicle} has taken the {@linkplain User} to its destination successfully.
     */
    public static final String STATUS_COMPLETED = "completed";
    /**
     * This status represents a {@linkplain Booking} that has been aborted,
     * so it means that a {@linkplain Vehicle} was unable to take the {@linkplain User} to its destination successfully.
     */
    public static final String STATUS_ABORTED = "aborted";

    private int id;
    private Date date;
    private String username;
    private String vehicleLicensePlate;
    private Position userPosition;
    private Position destinationPosition;
    private String bookingStatus;

    /**
     * Instantiates a new Booking.
     *
     * @param id                  the id
     * @param date                the date
     * @param username            the username
     * @param vehicleLicensePlate the vehicle license plate
     * @param userPosition        the user position
     * @param destinationPosition the destination position
     * @param bookingStatus       the booking status
     */
    public Booking(@JsonProperty("id") int id,
                   @JsonProperty("date") Date date,
                   @JsonProperty("username") String username,
                   @JsonProperty("vehicleLicensePlate") String vehicleLicensePlate,
                   @JsonProperty("userPosition") Position userPosition,
                   @JsonProperty("destinationPosition") Position destinationPosition,
                   @JsonProperty("bookingStatus") String bookingStatus) {
        this.id = id;
        this.date = date;
        this.username = username;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.userPosition = userPosition;
        this.destinationPosition = destinationPosition;
        this.bookingStatus = bookingStatus;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets vehicle license plate.
     *
     * @return the vehicle license plate
     */
    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    /**
     * Sets vehicle license plate.
     *
     * @param licensePlate the license plate
     */
    public void setVehicleLicensePlate(String licensePlate) {
        vehicleLicensePlate = licensePlate;
    }

    /**
     * Gets user position.
     *
     * @return the user position
     */
    public Position getUserPosition() {
        return userPosition;
    }

    /**
     * Sets user position.
     *
     * @param userPosition the user position
     */
    public void setUserPosition(Position userPosition) {
        this.userPosition = userPosition;
    }

    /**
     * Gets destination position.
     *
     * @return the destination position
     */
    public Position getDestinationPosition() {
        return destinationPosition;
    }

    /**
     * Sets destination position.
     *
     * @param destinationPosition the destination position
     */
    public void setDestinationPosition(Position destinationPosition) {
        this.destinationPosition = destinationPosition;
    }

    /**
     * Gets booking status.
     *
     * @return the booking status
     */
    public String getBookingStatus() {
        return bookingStatus;
    }

    /**
     * Sets booking status.
     *
     * @param bookingStatus the booking status
     */
    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        Booking booking = (Booking) o;
        return id == booking.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", date=" + date +
                ", username='" + username + '\'' +
                ", vehicleLicensePlate='" + vehicleLicensePlate + '\'' +
                ", userPosition=" + userPosition +
                ", destinationPosition=" + destinationPosition +
                ", bookingStatus='" + bookingStatus + '\'' +
                '}';
    }

}
