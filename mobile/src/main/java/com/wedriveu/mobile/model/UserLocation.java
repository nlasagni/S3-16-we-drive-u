package com.wedriveu.mobile.model;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *     Describes the user location, composed by the position where the user is, and the destination position the user chooses.
 * </p>
 *
 */
public class UserLocation {

    private Double GPSLatitude;
    private Double GPSLongitude;
    private Double addressLatitude;
    private Double addressLongitude;

    public UserLocation(Double GPSLatitude, Double GPSLongitude, Double addressLatitude, Double addressLongitude) {
        this.GPSLatitude = GPSLatitude;
        this.GPSLongitude = GPSLongitude;
        this.addressLatitude = addressLatitude;
        this.addressLongitude = addressLongitude;
    }

    public UserLocation(Double GPSLatitude, Double GPSLongitude) {
        this.GPSLatitude = GPSLatitude;
        this.GPSLongitude = GPSLongitude;
    }

    public UserLocation() {
    }

    public Double getGPSLatitude() {
        return GPSLatitude;
    }

    public void setGPSLatitude(Double GPSLatitude) {
        this.GPSLatitude = GPSLatitude;
    }

    public Double getGPSLongitude() {
        return GPSLongitude;
    }

    public void setGPSLongitude(Double GPSLongitude) {
        this.GPSLongitude = GPSLongitude;
    }

    public Double getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(Double addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public Double getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(Double addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public Map<Double, Double> getGPSPosition() {
        return (Map<Double, Double>) new HashMap<>().put(getGPSLatitude(), getGPSLongitude());
    }

    public Map<Double, Double> getDestinationPosition() {
        return (Map<Double, Double>) new HashMap<>().put(getAddressLongitude(), getAddressLongitude());
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "GPSLatitude=" + GPSLatitude +
                ", GPSLongitude=" + GPSLongitude +
                ", addressLatitude=" + addressLatitude +
                ", addressLongitude=" + addressLongitude +
                '}';
    }

}
