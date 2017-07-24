package com.wedriveu.mobile.model;

/**
 * Describes the user location, composed by the position where the user is, and the destination position the user chooses.
 *
 * @author Marco Baldassarri
 * @since 12/07/2017
 */
public class SchedulingLocation {

    private Double userLatitude;
    private Double userLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    public Double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(Double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public Double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(Double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "userLatitude=" + userLatitude +
                ", userLongitude=" + userLongitude +
                ", destinationLatitude=" + destinationLatitude +
                ", destinationLongitude=" + destinationLongitude +
                '}';
    }

}
