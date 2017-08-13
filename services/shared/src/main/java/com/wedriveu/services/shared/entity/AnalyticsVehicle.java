package com.wedriveu.services.shared.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Stefano Bernagozzi.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalyticsVehicle that = (AnalyticsVehicle) o;

        if (getLicensePlate() != null ? !getLicensePlate().equals(that.getLicensePlate()) : that.getLicensePlate() != null)
            return false;
        return getStatus() != null ? getStatus().equals(that.getStatus()) : that.getStatus() == null;
    }

    @Override
    public int hashCode() {
        int result = getLicensePlate() != null ? getLicensePlate().hashCode() : 0;
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AnalyticsVehicle{" +
                "licensePlate='" + licensePlate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
