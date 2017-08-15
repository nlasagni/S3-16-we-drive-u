package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Michele Donati on 09/08/2017.
 *
 * This class represents a "can drive?" response that a vehicle sends to the service.
 */

public class CanDriveResponse {

    private String license;
    private boolean ok;
    private double speed;

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public boolean getOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanDriveResponse that = (CanDriveResponse) o;

        if (ok != that.ok) return false;
        if (Double.compare(that.speed, speed) != 0) return false;
        return license.equals(that.license);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = license.hashCode();
        result = 31 * result + (ok ? 1 : 0);
        temp = Double.doubleToLongBits(speed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    @Override
    public String toString() {
        return "CanDriveResponse{" +
                "license='" + license + '\'' +
                ", ok=" + ok +
                ", speed=" + speed +
                '}';
    }
}
