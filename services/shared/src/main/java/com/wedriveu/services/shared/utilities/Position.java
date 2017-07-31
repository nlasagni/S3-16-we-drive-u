package com.wedriveu.services.shared.utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Michele on 17/07/2017.
 */
public class Position {

    private double latitude;
    private double longitude;

    public Position(@JsonProperty("latitude")double latitude,@JsonProperty("longitude") double longitude){
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

    public double getDistance(Position position) {
        return Math.sqrt(Math.pow(this.latitude - position.getLatitude(), 2)
                + Math.pow(this.longitude - position.getLongitude(), 2));
    }

}
