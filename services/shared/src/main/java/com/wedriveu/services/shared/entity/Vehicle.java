package com.wedriveu.services.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.util.Position;

import java.net.URL;
import java.util.Date;

/**
 * Represents the POJO Vehicle.
 *
 * @author Marco Baldassarri
 * @author Michele
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {

    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_BOOKED = "booked";
    public static final String STATUS_RECHARGING = "recharging";
    public static final String STATUS_BROKEN_STOLEN = "broken_stolen";
    public static final String STATUS_NETWORK_ISSUES = "net_issues";
    public static final String NO_ELIGIBLE_VEHICLE_RESPONSE = "No vehicles nearby, " +
            "please try again later or change your address";

    private String licensePlate;
    private String status;
    private Position position;
    private Date lastUpdate;
    private String name;
    private String description;
    private URL imageUrl;
    private String notEligibleVehicleFound;

    public Vehicle(@JsonProperty String licensePlate,
                   @JsonProperty String status,
                   @JsonProperty Position position,
                   @JsonProperty Date lastUpdate,
                   @JsonProperty String name,
                   @JsonProperty String description,
                   @JsonProperty URL imageUrl) {
        this.licensePlate = licensePlate;
        this.status = status;
        this.position = position;
        this.lastUpdate = lastUpdate;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Vehicle(@JsonProperty String licensePlate,
                   @JsonProperty String status,
                   @JsonProperty Position position,
                   @JsonProperty Date lastUpdate) {
        this.licensePlate = licensePlate;
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

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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
                "licensePlate='" + licensePlate + '\'' +
                ", state='" + status + '\'' +
                ", position=" + position +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vehicle)) {
            return false;
        }
        Vehicle vehicle = (Vehicle) other;
        return licensePlate != null && licensePlate.equals(vehicle.licensePlate);
    }

    @Override
    public int hashCode() {
        return licensePlate.hashCode();
    }
}
