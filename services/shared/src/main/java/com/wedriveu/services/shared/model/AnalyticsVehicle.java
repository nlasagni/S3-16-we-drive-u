package com.wedriveu.services.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the {@linkplain Vehicle} model for the analytics service.
 *
 * @author Stefano Bernagozzi.
 */
public class AnalyticsVehicle {

    private String licensePlate;
    private String status;

    /**
     * Instantiates a new Analytics vehicle.
     *
     * @param licensePlate the license plate
     * @param status       the status
     */
    public AnalyticsVehicle(@JsonProperty("licensePlate") String licensePlate,
                            @JsonProperty("status") String status) {
        this.licensePlate = licensePlate;
        this.status = status;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnalyticsVehicle)) {
            return false;
        }
        AnalyticsVehicle that = (AnalyticsVehicle) o;
        return (licensePlate != null ? licensePlate.equals(that.licensePlate) : that.licensePlate == null) &&
                (status != null ? status.equals(that.status) : that.status == null);
    }

    @Override
    public int hashCode() {
        int result = licensePlate != null ? licensePlate.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
