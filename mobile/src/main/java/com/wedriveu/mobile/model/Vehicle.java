package com.wedriveu.mobile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.util.Position;

/**
 * Describes a Vehicle
 *
 * @author Marco Baldassarri
 * @since 12/07/2017
 */
public class Vehicle {

    private String licencePlate;
    private String vehicleName;
    private String description;
    private String pictureURL;
    private String arriveAtUserTime;
    private String arriveAtDestinationTime;
    private Position position;

    public Vehicle(@JsonProperty("licencePlate") String licencePlate,
                   @JsonProperty("vehicleName") String vehicleName,
                   @JsonProperty("description") String description,
                   @JsonProperty("pictureURL") String pictureURL,
                   @JsonProperty("arriveAtUserTime") String arriveAtUserTime,
                   @JsonProperty("arriveAtDestinationTime") String arriveAtDestinationTime) {
        this.licencePlate = licencePlate;
        this.vehicleName = vehicleName;
        this.description = description;
        this.pictureURL = pictureURL;
        this.arriveAtUserTime = arriveAtUserTime;
        this.arriveAtDestinationTime = arriveAtDestinationTime;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public String getArriveAtUserTime() {
        return arriveAtUserTime;
    }

    public String getArriveAtDestinationTime() {
        return arriveAtDestinationTime;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "licencePlate='" + licencePlate + '\'' +
                ", vehicleName='" + vehicleName + '\'' +
                ", description='" + description + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", arriveAtUserTime='" + arriveAtUserTime + '\'' +
                ", arriveAtDestinationTime='" + arriveAtDestinationTime + '\'' +
                '}';
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
