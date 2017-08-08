package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.utilities.Position;

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
    @JsonProperty(STATE)
    private String state;
    @JsonProperty(POSITION)
    private Position position;
    @JsonProperty(LAST_UPDATE)
    private Date lastUpdate;

    public Vehicle(String carLicencePlate,
                   String state,
                   Position position,
                   Date lastUpdate) {
        this.carLicencePlate = carLicencePlate;
        this.state = state;
        this.position = position;
        this.lastUpdate = lastUpdate;
    }

    public String getCarLicencePlate() {
        return carLicencePlate;
    }

    public void setCarLicencePlate(String carLicencePlate) {
        this.carLicencePlate = carLicencePlate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
                ", state='" + state + '\'' +
                ", position=" + position +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
