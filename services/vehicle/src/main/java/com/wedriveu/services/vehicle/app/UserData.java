package com.wedriveu.services.vehicle.app;

/**
 * Created by Marco on 02/08/2017.
 */
public class UserData {

    private String userLatitude;
    private String userLongitude;
    private String destLatitude;
    private String destLongitude;
    private String username;

    public String getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(String userLatitude) {
        this.userLatitude = userLatitude;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(String userLongitude) {
        this.userLongitude = userLongitude;
    }

    public String getDestLatitude() {
        return destLatitude;
    }

    public void setDestLatitude(String destLatitude) {
        this.destLatitude = destLatitude;
    }

    public String getDestLongitude() {
        return destLongitude;
    }

    public void setDestLongitude(String destLongitude) {
        this.destLongitude = destLongitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
