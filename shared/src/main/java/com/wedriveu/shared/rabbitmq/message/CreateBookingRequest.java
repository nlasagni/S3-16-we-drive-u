package com.wedriveu.shared.rabbitmq.message;

import com.wedriveu.shared.util.Position;

/**
 * A booking request from a client that wants to travel from its position
 * to a destination position with a vehicle previously selected by the system.
 *
 * @author Nicola Lasagni on 12/08/2017.
 */
public class CreateBookingRequest {

    private String username;
    private String licensePlate;
    private Position userPosition;
    private Position destinationPosition;

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets vehicle license plate.
     *
     * @return the license plate
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Sets vehicle license plate.
     *
     * @param licensePlate the license plate
     */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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
     * Gets booking destination position.
     *
     * @return the booking destination position
     */
    public Position getDestinationPosition() {
        return destinationPosition;
    }

    /**
     * Sets booking destination position.
     *
     * @param destinationPosition the booking destination position
     */
    public void setDestinationPosition(Position destinationPosition) {
        this.destinationPosition = destinationPosition;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CreateBookingRequest)) {
            return false;
        }
        CreateBookingRequest otherRequest = (CreateBookingRequest) other;
        return (username != null ? username.equals(otherRequest.username) : otherRequest.username == null) &&
                (licensePlate != null
                        ? licensePlate.equals(otherRequest.licensePlate)
                        : otherRequest.licensePlate == null) &&
                (userPosition != null
                        ? userPosition.equals(otherRequest.userPosition)
                        : otherRequest.userPosition == null) &&
                (destinationPosition != null
                        ? destinationPosition.equals(otherRequest.destinationPosition)
                        : otherRequest.destinationPosition == null);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (licensePlate != null ? licensePlate.hashCode() : 0);
        result = 31 * result + (userPosition != null ? userPosition.hashCode() : 0);
        result = 31 * result + (destinationPosition != null ? destinationPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CreateBookingRequest{" +
                "username='" + username + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", userPosition=" + userPosition +
                ", destinationPosition=" + destinationPosition +
                '}';
    }
}
