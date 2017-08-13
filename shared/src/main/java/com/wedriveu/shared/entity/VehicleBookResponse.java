package com.wedriveu.shared.entity;

/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents a "book" response that a vehicle sends to the service.
 */

public class VehicleBookResponse {

    private boolean booked;

    public boolean getBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleBookResponse that = (VehicleBookResponse) o;

        return booked == that.booked;
    }

    @Override
    public int hashCode() {
        return (booked ? 1 : 0);
    }

    @Override
    public String toString() {
        return "VehicleBookResponse{" +
                "booked=" + booked +
                '}';
    }

}
