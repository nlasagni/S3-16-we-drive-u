package com.wedriveu.services.shared.utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Marco Baldassarri
 * @author Michele
 * @since  17/07/2017
 */

public class Position {

    @JsonProperty
    private double latitude;
    @JsonProperty
    private double longitude;

    public Position() { }

    public Position(double latitude,
                    double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getEuclideanDistance(Position position) {
        return Math.sqrt(Math.pow(this.latitude - position.getLatitude(), 2)
                + Math.pow(this.longitude - position.getLongitude(), 2));
    }

}
