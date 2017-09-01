package com.wedriveu.services.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.util.Position;

import java.util.Date;

/**
 * Represents the Vehicle, which is capable of serving {@linkplain User}s trip requests.
 *
 * @author Marco Baldassarri
 * @author Michele
 */
public class Vehicle {

    private String licensePlate;
    private String status;
    private Position position;
    private Date lastUpdate;
    private String name;
    private String description;
    private String imageUrl;
    private String notEligibleVehicleFound;

    /**
     * Instantiates a new Vehicle.
     *
     * @param licensePlate the license plate
     * @param status       the status
     * @param position     the position
     * @param lastUpdate   the last update
     * @param name         the name
     * @param description  the description
     * @param imageUrl     the image url
     */
    public Vehicle(@JsonProperty String licensePlate,
                   @JsonProperty String status,
                   @JsonProperty Position position,
                   @JsonProperty Date lastUpdate,
                   @JsonProperty String name,
                   @JsonProperty String description,
                   @JsonProperty String imageUrl) {
        this.licensePlate = licensePlate;
        this.status = status;
        this.position = position;
        this.lastUpdate = lastUpdate;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    /**
     * Instantiates a new Vehicle.
     *
     * @param licensePlate the license plate
     * @param status       the status
     * @param position     the position
     * @param lastUpdate   the last update
     */
    public Vehicle(@JsonProperty String licensePlate,
                   @JsonProperty String status,
                   @JsonProperty Position position,
                   @JsonProperty Date lastUpdate) {
        this.licensePlate = licensePlate;
        this.status = status;
        this.position = position;
        this.lastUpdate = lastUpdate;
    }

    /**
     * Instantiates a new Vehicle.
     */
    public Vehicle() {
    }

    /**
     * Gets not eligible vehicle found.
     *
     * @return the not eligible vehicle found
     */
    public String getNotEligibleVehicleFound() {
        return notEligibleVehicleFound;
    }

    /**
     * Sets not eligible vehicle found.
     *
     * @param notEligibleVehicleFound the not eligible vehicle found
     */
    public void setNotEligibleVehicleFound(String notEligibleVehicleFound) {
        this.notEligibleVehicleFound = notEligibleVehicleFound;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets image url.
     *
     * @return the image url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets image url.
     *
     * @param imageUrl the image url
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets license plate.
     *
     * @return the license plate
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Sets license plate.
     *
     * @param licensePlate the license plate
     */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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
     * Gets last update.
     *
     * @return the last update
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets last update.
     *
     * @param lastUpdate the last update
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
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
