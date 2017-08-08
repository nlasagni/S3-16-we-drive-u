package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.utilities.Position;

import java.net.URL;
import java.util.Date;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * @author Marco Baldassarri
 * @author Michele
 * @since 02/08/2017
 */
public class Vehicle {

    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_BOOKED = "booked";
    public static final String STATUS_RECHARGING = "recharging";
    public static final String STATUS_BROKEN_STOLEN = "broken_stolen";
    public static final String STATUS_NETWORK_ISSUES = "net_issues";

    @JsonProperty(CAR_LICENCE_PLATE)
    private String carLicencePlate;
    @JsonProperty(STATUS)
    private String status;
    @JsonProperty(POSITION)
    private Position position;
    @JsonProperty(LAST_UPDATE)
    private Date lastUpdate;
    @JsonProperty(VEHICLE_NAME)
    private String name;
    @JsonProperty(VEHICLE_DESCRIPTION)
    private String description;
    @JsonProperty(IMAGE_URL)
    private URL imageUrl;

    public Vehicle(String carLicencePlate,
                   String status,
                   Position position,
                   Date lastUpdate) {
        this.carLicencePlate = carLicencePlate;
        this.status = status;
        this.position = position;
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCarLicencePlate() {
        return carLicencePlate;
    }

    public void setCarLicencePlate(String carLicencePlate) {
        this.carLicencePlate = carLicencePlate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "carLicencePlate='" + carLicencePlate + '\'' +
                ", state='" + status + '\'' +
                ", position=" + position +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
