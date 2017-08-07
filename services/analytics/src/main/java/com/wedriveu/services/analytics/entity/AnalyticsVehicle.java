package com.wedriveu.services.analytics.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author Stefano Bernagozzi.
 */
public class AnalyticsVehicle {

    private String licensePlate;
    private String status;

    public AnalyticsVehicle(@JsonProperty("licensePlate") String licensePlate,
                            @JsonProperty("status") String status){
        this.licensePlate = licensePlate;
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getStatus() {
        return status;
    }

}
