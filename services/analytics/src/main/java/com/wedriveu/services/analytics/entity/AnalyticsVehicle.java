package com.wedriveu.services.analytics.entity;

public class AnalyticsVehicle {
    private String licensePlate;
    private String status;

    public AnalyticsVehicle(String licensePlate, String status){
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
