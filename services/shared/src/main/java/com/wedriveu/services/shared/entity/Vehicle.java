package com.wedriveu.services.shared.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.entity.Position;
import java.net.URL;
import java.util.Date;

/**
 *
 * Represents the POJO Vehicle.
 *
 * @author Marco Baldassarri
 * @author Michele
 */
public class Vehicle {

    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_BOOKED = "booked";
    public static final String STATUS_RECHARGING = "recharging";
    public static final String STATUS_BROKEN_STOLEN = "broken_stolen";
    public static final String STATUS_NETWORK_ISSUES = "net_issues";
    public static final String NO_ELIGIBLE_VEHICLE_RESPONSE = "No vehicles nearby, " +
                                                        "please try again later or change your address";

    @JsonProperty
    private String carLicencePlate;
    @JsonProperty
    private String status;
    @JsonProperty
    private Position position;
    @JsonProperty
    private Date lastUpdate;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private URL imageUrl;
    @JsonProperty
    private String notEligibleVehicleFound;

    public Vehicle(String carLicencePlate,
                   String status,
                   Position position,
                   Date lastUpdate,
                   String name,
                   String description,
                   URL imageUrl) {
        this.carLicencePlate = carLicencePlate;
        this.status = status;
        this.position = position;
        this.lastUpdate = lastUpdate;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Vehicle(@JsonProperty String carLicencePlate,
                   @JsonProperty String status,
                   @JsonProperty Position position,
                   @JsonProperty Date lastUpdate) {
        this.carLicencePlate = carLicencePlate;
        this.status = status;
        this.position = position;
        this.lastUpdate = lastUpdate;
    }

    public Vehicle() {
    }

    public String getNotEligibleVehicleFound() {
        return notEligibleVehicleFound;
    }

    public void setNotEligibleVehicleFound(String notEligibleVehicleFound) {
        this.notEligibleVehicleFound = notEligibleVehicleFound;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return carLicencePlate.equals(vehicle.carLicencePlate);
    }

    @Override
    public int hashCode() {
        return carLicencePlate.hashCode();
    }
}
