package com.wedriveu.services.shared.utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Michele on 17/07/2017.
 */
public class Position {

    private double latitude;
    private double longitude;

    public Position(@JsonProperty("latitude") double latitude,
                    @JsonProperty("longitude") double longitude) {
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

    // I will not delete this method yet for explaining the difference to others and why this is useless
    public double getEuclideanDistance(Position position) {
        return Math.sqrt(Math.pow(this.latitude - position.getLatitude(), 2)
                + Math.pow(this.longitude - position.getLongitude(), 2));
    }

    public double getDistanceInKm(Position position) {
        double earthRadius = 6372.795477598;
        return earthRadius * Math.acos(Math.sin(this.latitude) * Math.sin(position.getLatitude())
                            + Math.cos(this.latitude) * Math.cos(position.getLatitude())
                            * Math.cos(this.longitude - position.getLongitude()));
    }

}
