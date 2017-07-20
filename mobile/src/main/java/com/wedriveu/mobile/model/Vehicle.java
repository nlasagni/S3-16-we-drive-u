package com.wedriveu.mobile.model;

import com.google.android.gms.location.places.Place;

import java.net.URL;

/**
 * <p>
 *     Describes a Vehicle
 * </p>
 */
public class Vehicle {

    private String licencePlate;
    private String pictureURL;
    private String description;
    private String positionAddress;

    public Vehicle(String licencePlate, String pictureURL, String description, String positionAddress) {
        this.licencePlate = licencePlate;
        this.pictureURL = pictureURL;
        this.description = description;
        this.positionAddress = positionAddress;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPositionAddress() {
        return positionAddress;
    }

    public void setPositionAddress(String positionAddress) {
        this.positionAddress = positionAddress;
    }

}
