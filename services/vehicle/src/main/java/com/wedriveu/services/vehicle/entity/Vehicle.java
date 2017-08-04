package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.services.shared.utilities.Position;

import java.util.Date;

import static com.wedriveu.services.shared.utilities.Constants.*;


/**
 * Created by Michele on 12/07/2017.
 * marco
 */
public class Vehicle {

    @JsonProperty(CAR_LICENCE_PLATE)
    private String carLicencePlate;
    @JsonProperty(STATE)
    private String state;
    @JsonProperty(POSITION)
    private Position position;
    @JsonProperty(LAST_UPDATE)
    private Date lastUpdate;

    public Vehicle(@JsonProperty(CAR_LICENCE_PLATE) String carLicencePlate,
                   @JsonProperty(STATE) String state,
                   @JsonProperty(POSITION) Position position,
                   @JsonProperty(LAST_UPDATE) Date lastUpdate) {
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
