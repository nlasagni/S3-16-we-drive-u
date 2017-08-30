package com.wedriveu.mobile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.util.Position;

/**
 * The Booking of a trip chosen by the user.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public class Booking {

    private String licensePlate;
    private String destinationAddress;
    private Position userPosition;
    private Position destinationPosition;

    /**
     * Instantiates a new Booking.
     *
     * @param licensePlate        the vehicle license plate
     * @param destinationAddress  the destination address
     * @param userPosition        the user position
     * @param destinationPosition the destination position
     */
    public Booking(@JsonProperty("licensePlate") String licensePlate,
                   @JsonProperty("destinationAddress") String destinationAddress,
                   @JsonProperty("userPosition") Position userPosition,
                   @JsonProperty("destinationPosition") Position destinationPosition) {
        this.licensePlate = licensePlate;
        this.destinationAddress = destinationAddress;
        this.userPosition = userPosition;
        this.destinationPosition = destinationPosition;
    }

    /**
     * Gets the destination address.
     *
     * @return the destination address
     */
    public String getDestinationAddress() {
        return destinationAddress;
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
     * Gets user position.
     *
     * @return the user position
     */
    public Position getUserPosition() {
        return userPosition;
    }

    /**
     * Gets destination position.
     *
     * @return the destination position
     */
    public Position getDestinationPosition() {
        return destinationPosition;
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
        return (licensePlate != null ? licensePlate.equals(booking.licensePlate) : booking.licensePlate == null) &&
                (destinationAddress != null ? destinationAddress.equals(booking.destinationAddress) : booking.destinationAddress == null) &&
                (userPosition != null ? userPosition.equals(booking.userPosition) : booking.userPosition == null) &&
                (destinationPosition != null
                        ? destinationPosition.equals(booking.destinationPosition)
                        : booking.destinationPosition == null);
    }

    @Override
    public int hashCode() {
        int result = licensePlate != null ? licensePlate.hashCode() : 0;
        result = 31 * result + (destinationAddress != null ? destinationAddress.hashCode() : 0);
        result = 31 * result + (userPosition != null ? userPosition.hashCode() : 0);
        result = 31 * result + (destinationPosition != null ? destinationPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "licensePlate='" + licensePlate + '\'' +
                ", destinationAddress='" + destinationAddress + '\'' +
                ", userPosition=" + userPosition +
                ", destinationPosition=" + destinationPosition +
                '}';
    }

}
