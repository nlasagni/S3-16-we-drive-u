package com.wedriveu.shared.rabbitmq.message;

/**
 * class sent for request entering a vehicle
 *
 * @author Michele Donati on 17/08/2017.
 */

public class EnterVehicleRequest {

    private String licensePlate;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String newLicensePlate) {
        this.licensePlate = newLicensePlate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnterVehicleRequest that = (EnterVehicleRequest) o;

        return licensePlate.equals(that.licensePlate);
    }

    @Override
    public int hashCode() {
        return licensePlate.hashCode();
    }

    @Override
    public String toString() {
        return "EnterVehicleRequest{" +
                "licensePlate='" + licensePlate + '\'' +
                '}';
    }

}
