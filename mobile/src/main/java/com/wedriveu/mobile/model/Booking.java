package com.wedriveu.mobile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.util.Position;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class Booking {

    private String licensePlate;
    private Position userPosition;
    private Position destinationPosition;

    public Booking(@JsonProperty("licensePlate") String licensePlate,
                   @JsonProperty("userPosition") Position userPosition,
                   @JsonProperty("destinationPosition") Position destinationPosition) {
        this.licensePlate = licensePlate;
        this.userPosition = userPosition;
        this.destinationPosition = destinationPosition;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public Position getUserPosition() {
        return userPosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

}
