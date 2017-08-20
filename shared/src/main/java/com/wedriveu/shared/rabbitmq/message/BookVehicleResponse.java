package com.wedriveu.shared.rabbitmq.message;


/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents a "book" response that a vehicle sends to the service.
 */

public class BookVehicleResponse {

    private String licensePlate;
    private boolean booked;
    private double speed;
    private long driveTimeToUser;
    private long driveTimeToDestination;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getDriveTimeToUser() {
        return driveTimeToUser;
    }

    public void setDriveTimeToUser(long driveTimeToUser) {
        this.driveTimeToUser = driveTimeToUser;
    }

    public long getDriveTimeToDestination() {
        return driveTimeToDestination;
    }

    public void setDriveTimeToDestination(long driveTimeToDestination) {
        this.driveTimeToDestination = driveTimeToDestination;
    }

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

        BookVehicleResponse that = (BookVehicleResponse) o;

        if (booked != that.booked) return false;
        if (Double.compare(that.speed, speed) != 0) return false;
        if (driveTimeToUser != that.driveTimeToUser) return false;
        if (driveTimeToDestination != that.driveTimeToDestination) return false;
        return licensePlate != null ? licensePlate.equals(that.licensePlate) : that.licensePlate == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = licensePlate != null ? licensePlate.hashCode() : 0;
        result = 31 * result + (booked ? 1 : 0);
        temp = Double.doubleToLongBits(speed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (driveTimeToUser ^ (driveTimeToUser >>> 32));
        result = 31 * result + (int) (driveTimeToDestination ^ (driveTimeToDestination >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "BookVehicleResponse{" +
                "licensePlate='" + licensePlate + '\'' +
                ", booked=" + booked +
                ", speed=" + speed +
                ", driveTimeToUser=" + driveTimeToUser +
                ", driveTimeToDestination=" + driveTimeToDestination +
                '}';
    }

}
