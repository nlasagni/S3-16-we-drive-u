package com.wedriveu.mobile.service.scheduling.model;

/**
 * <p>
 *     Describes the Vehicle as returned from the VehicleService.
 *     Some of the fields won't be used for the Android Application
 * </p>
 */
public class VehicleResponse {

    private String licencePlate;
    private String vehicleName;
    private String description;
    private String pictureURL;
    private String arriveAtUserTime;
    private String arriveAtDestinationTime;
    private String lastUpdate;
    private String userPosition;
    private String destinationPosition;

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getArriveAtUserTime() {
        return arriveAtUserTime;
    }

    public void setArriveAtUserTime(String arriveAtUserTime) {
        this.arriveAtUserTime = arriveAtUserTime;
    }

    public String getArriveAtDestinationTime() {
        return arriveAtDestinationTime;
    }

    public void setArriveAtDestinationTime(String arriveAtDestinationTime) {
        this.arriveAtDestinationTime = arriveAtDestinationTime;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(String userPosition) {
        this.userPosition = userPosition;
    }

    public String getDestinationPosition() {
        return destinationPosition;
    }

    public void setDestinationPosition(String destinationPosition) {
        this.destinationPosition = destinationPosition;
    }

}
