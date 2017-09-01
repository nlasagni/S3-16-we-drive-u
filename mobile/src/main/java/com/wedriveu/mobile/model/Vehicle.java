package com.wedriveu.mobile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.util.Position;

/**
 * The vehicle chosen by the WeDriveU service that will take the {@link User} to
 * its destination.
 *
 * @author Marco Baldassarri
 * @author Nicola Lasagni
 */
public class Vehicle {

    private String licencePlate;
    private String status;
    private String vehicleName;
    private String description;
    private String pictureURL;
    private String arriveAtUserTime;
    private String arriveAtDestinationTime;
    private Position position;

    /**
     * Instantiates a new Vehicle.
     *
     * @param licencePlate            the licence plate
     * @param status                  the status
     * @param vehicleName             the vehicle name
     * @param description             the description
     * @param pictureURL              the picture url
     * @param arriveAtUserTime        the time when the vehicle will arrive at the user
     * @param arriveAtDestinationTime the time when the vehicle will arrive at the destination
     */
    public Vehicle(@JsonProperty("licencePlate") String licencePlate,
                   @JsonProperty("status") String status,
                   @JsonProperty("vehicleName") String vehicleName,
                   @JsonProperty("description") String description,
                   @JsonProperty("pictureURL") String pictureURL,
                   @JsonProperty("arriveAtUserTime") String arriveAtUserTime,
                   @JsonProperty("arriveAtDestinationTime") String arriveAtDestinationTime) {
        this.licencePlate = licencePlate;
        this.status = status;
        this.vehicleName = vehicleName;
        this.description = description;
        this.pictureURL = pictureURL;
        this.arriveAtUserTime = arriveAtUserTime;
        this.arriveAtDestinationTime = arriveAtDestinationTime;
    }

    /**
     * Gets licence plate.
     *
     * @return the licence plate
     */
    public String getLicencePlate() {
        return licencePlate;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets vehicle name.
     *
     * @return the vehicle name
     */
    public String getVehicleName() {
        return vehicleName;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets picture url.
     *
     * @return the picture url
     */
    public String getPictureURL() {
        return pictureURL;
    }

    /**
     * Gets  the time when the vehicle will arrive at the user.
     *
     * @return  the time when the vehicle will arrive at the user
     */
    public String getArriveAtUserTime() {
        return arriveAtUserTime;
    }

    /**
     * Gets the time when the vehicle will arrive at the destination.
     *
     * @return the time when the vehicle will arrive at the destination
     */
    public String getArriveAtDestinationTime() {
        return arriveAtDestinationTime;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets position.
     *
     * @param position the position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vehicle)) {
            return false;
        }
        Vehicle vehicle = (Vehicle) o;
        return (licencePlate != null ? licencePlate.equals(vehicle.licencePlate) : vehicle.licencePlate == null) &&
                (status != null ? status.equals(vehicle.status) : vehicle.status == null) &&
                (vehicleName != null ? vehicleName.equals(vehicle.vehicleName) : vehicle.vehicleName == null) &&
                (description != null ? description.equals(vehicle.description) : vehicle.description == null) &&
                (pictureURL != null ? pictureURL.equals(vehicle.pictureURL) : vehicle.pictureURL == null) &&
                (arriveAtUserTime != null
                        ? arriveAtUserTime.equals(vehicle.arriveAtUserTime)
                        : vehicle.arriveAtUserTime == null) &&
                (arriveAtDestinationTime != null
                        ? arriveAtDestinationTime.equals(vehicle.arriveAtDestinationTime)
                        : vehicle.arriveAtDestinationTime == null) &&
                (position != null ? position.equals(vehicle.position) : vehicle.position == null);
    }

    @Override
    public int hashCode() {
        int result = licencePlate != null ? licencePlate.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (vehicleName != null ? vehicleName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (pictureURL != null ? pictureURL.hashCode() : 0);
        result = 31 * result + (arriveAtUserTime != null ? arriveAtUserTime.hashCode() : 0);
        result = 31 * result + (arriveAtDestinationTime != null ? arriveAtDestinationTime.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "licencePlate='" + licencePlate + '\'' +
                ", status='" + status + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", description='" + description + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", arriveAtUserTime='" + arriveAtUserTime + '\'' +
                ", arriveAtDestinationTime='" + arriveAtDestinationTime + '\'' +
                ", position=" + position +
                '}';
    }

}
