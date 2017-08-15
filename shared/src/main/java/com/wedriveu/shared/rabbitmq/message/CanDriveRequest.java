package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Michele Donati on 09/08/2017.
 *
 * This class represents a "can drive?" request that the service sends to the available vehicles.
 */

public class CanDriveRequest {

    private double distanceInKm;
    private String username;

    public double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanDriveRequest that = (CanDriveRequest) o;

        if (Double.compare(that.distanceInKm, distanceInKm) != 0) return false;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(distanceInKm);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + username.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CanDriveRequest{" +
                "distanceInKm=" + distanceInKm +
                ", username='" + username + '\'' +
                '}';
    }

}
