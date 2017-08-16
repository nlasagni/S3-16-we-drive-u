package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents a "book" response that a vehicle sends to the service.
 */

public class VehicleBookResponse {

    private boolean booked;
    private double speed;

    public boolean getBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleBookResponse that = (VehicleBookResponse) o;

        if (booked != that.booked) return false;
        return Double.compare(that.speed, speed) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (booked ? 1 : 0);
        temp = Double.doubleToLongBits(speed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "VehicleBookResponse{" +
                "booked=" + booked +
                ", speed=" + speed +
                '}';
    }
}
