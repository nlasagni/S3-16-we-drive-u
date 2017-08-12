package com.wedriveu.shared.utils;

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

    public static double getDistanceInKm(Position from, Position to) {
        double earthRadius = 6372.795477598;
        return earthRadius * Math.acos(Math.sin(from.getLatitude()) * Math.sin(to.getLatitude())
                + Math.cos(from.getLatitude()) * Math.cos(to.getLatitude())
                * Math.cos(from.getLatitude() - to.getLongitude()));
    }

    public double getDistanceInKm(Position to) {
        double earthRadius = 6372.795477598;
        return earthRadius * Math.acos(Math.sin(this.latitude) * Math.sin(to.getLatitude())
                + Math.cos(this.latitude) * Math.cos(to.getLatitude())
                * Math.cos(this.longitude - to.getLongitude()));
    }

    public static boolean isInRange(Position userPosition, Position vehiclePosition) {
        return userPosition.getEuclideanDistance(vehiclePosition) < 20;
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
