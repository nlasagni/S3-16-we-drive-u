package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.utilities.Position;

import java.util.Date;

/**
 * Created by Michele on 12/07/2017.
 */
public class Vehicle {
    private String carLicencePlate;
    private String state;
    private Position position;
    private Date lastUpdate;

    public Vehicle(@JsonProperty("carLicencePlate")String carLicencePlate,
                   @JsonProperty("state")String state,
                   @JsonProperty("position") Position position,
                   @JsonProperty("lastUpdate")Date lastUpdate) {
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
}
