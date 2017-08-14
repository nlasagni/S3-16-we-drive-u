package com.wedriveu.shared.rabbitmq.message;

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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Position otherPosition = (Position) other;
        return Double.compare(otherPosition.latitude, latitude) == 0 &&
                Double.compare(otherPosition.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getLatitude());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}
