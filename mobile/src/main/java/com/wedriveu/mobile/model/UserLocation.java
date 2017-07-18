package com.wedriveu.mobile.model;

/**
 * Created by Marco on 18/07/2017.
 */
public class UserLocation {

    private Long GPSLatitude;
    private Long GPSLongitude;
    private Long AddressLatitude;
    private Long AddressLongitude;

    public UserLocation(Long GPSLatitude, Long GPSLongitude, Long addressLatitude, Long addressLongitude) {
        this.GPSLatitude = GPSLatitude;
        this.GPSLongitude = GPSLongitude;
        AddressLatitude = addressLatitude;
        AddressLongitude = addressLongitude;
    }

    public Long getGPSLatitude() {
        return GPSLatitude;
    }

    public void setGPSLatitude(Long GPSLatitude) {
        this.GPSLatitude = GPSLatitude;
    }

    public Long getGPSLongitude() {
        return GPSLongitude;
    }

    public void setGPSLongitude(Long GPSLongitude) {
        this.GPSLongitude = GPSLongitude;
    }

    public Long getAddressLatitude() {
        return AddressLatitude;
    }

    public void setAddressLatitude(Long addressLatitude) {
        AddressLatitude = addressLatitude;
    }

    public Long getAddressLongitude() {
        return AddressLongitude;
    }

    public void setAddressLongitude(Long addressLongitude) {
        AddressLongitude = addressLongitude;
    }
}
