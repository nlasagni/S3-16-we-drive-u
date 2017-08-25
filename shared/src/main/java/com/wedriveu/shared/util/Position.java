package com.wedriveu.shared.util;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Marco Baldassarri
 * @author Michele Donati
 * @since  17/07/2017
 */

public class Position {

    @JsonProperty
    private double latitude;
    @JsonProperty
    private double longitude;

    public Position() { }

    /**
     * @param latitude the latitude express in decimal degrees
     * @param longitude the longitude express in decimal degrees
     */
    public Position(double latitude,
                    double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return the latitude express in decimal degrees
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude express in decimal degrees
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude express in decimal degrees
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude express in decimal degrees
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * returns the distance between two points
     * @param from the starting point
     * @param to the ending point
     * @return the distance between the two points expressed in kilometers
     */
    public static double getDistanceInKm(Position from, Position to) {
        return PositionUtils.getDistanceInKm(from, to);
    }

    /**
     * @param to a position point
     * @return the distance between the position inside the class and the position given expressed in kilometers
     */
    public double getDistanceInKm(Position to) {
        return PositionUtils.getDistanceInKm(new Position(this.latitude, this.longitude), to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (Double.compare(position.latitude, latitude) != 0) return false;
        return Double.compare(position.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
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
