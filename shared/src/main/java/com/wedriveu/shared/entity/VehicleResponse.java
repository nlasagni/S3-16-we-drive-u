package com.wedriveu.shared.entity;

/**
 *
 *  Describes the Vehicle as returned from the VehicleService.
 *  Some of the fields won't be used for the Android Application
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


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        VehicleResponse otherResponse = (VehicleResponse) other;
        return (licencePlate != null ? licencePlate.equals(otherResponse.licencePlate) : otherResponse.licencePlate == null) &&
                (vehicleName != null ? vehicleName.equals(otherResponse.vehicleName) : otherResponse.vehicleName == null) &&
                (description != null ? description.equals(otherResponse.description) : otherResponse.description == null) &&
                (pictureURL != null ? pictureURL.equals(otherResponse.pictureURL) : otherResponse.pictureURL == null) &&
                (arriveAtUserTime != null ? arriveAtUserTime.equals(otherResponse.arriveAtUserTime) : otherResponse.arriveAtUserTime == null) &&
                (arriveAtDestinationTime != null ? arriveAtDestinationTime.equals(otherResponse.arriveAtDestinationTime) : otherResponse.arriveAtDestinationTime == null) &&
                (lastUpdate != null ? lastUpdate.equals(otherResponse.lastUpdate) : otherResponse.lastUpdate == null) &&
                (userPosition != null ? userPosition.equals(otherResponse.userPosition) : otherResponse.userPosition == null) &&
                (destinationPosition != null ? destinationPosition.equals(otherResponse.destinationPosition) : otherResponse.destinationPosition == null);
    }

    @Override
    public int hashCode() {
        int result = licencePlate != null ? licencePlate.hashCode() : 0;
        result = 31 * result + (vehicleName != null ? vehicleName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (pictureURL != null ? pictureURL.hashCode() : 0);
        result = 31 * result + (arriveAtUserTime != null ? arriveAtUserTime.hashCode() : 0);
        result = 31 * result + (arriveAtDestinationTime != null ? arriveAtDestinationTime.hashCode() : 0);
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        result = 31 * result + (userPosition != null ? userPosition.hashCode() : 0);
        result = 31 * result + (destinationPosition != null ? destinationPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleResponse{" +
                "licencePlate='" + licencePlate + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", description='" + description + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", arriveAtUserTime='" + arriveAtUserTime + '\'' +
                ", arriveAtDestinationTime='" + arriveAtDestinationTime + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", userPosition='" + userPosition + '\'' +
                ", destinationPosition='" + destinationPosition + '\'' +
                '}';
    }

}