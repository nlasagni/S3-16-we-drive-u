package com.wedriveu.services.shared.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.rabbitmq.message.Position;

import java.util.Date;

/**
 * @author Michele on 12/07/2017.
 * @author Nicola Lasagni on 31/07/2017
 */

public class Booking {

    public static final String STATUS_STARTED = "started";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETED = "completed";

    private int id;
    private Date date;
    private String username;
    private String vehicleLicensePlate;
    private Position userPosition;
    private Position destinationPosition;
    private String bookingStatus;

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

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    public Position getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(Position userPosition) {
        this.userPosition = userPosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

    public void setDestinationPosition(Position destinationPosition) {
        this.destinationPosition = destinationPosition;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

}
