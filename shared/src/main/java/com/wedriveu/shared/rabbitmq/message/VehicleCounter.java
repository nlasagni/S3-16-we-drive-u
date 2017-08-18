package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Stefano Bernagozzi
 */

public class VehicleCounter {
    private int available;
    private int broken;
    private int booked;
    private int recharging;
    private int stolen;

    public void increaseAvailable() {
        available++;
    }

    public void increaseBroken() {
        broken++;
    }

    public void increaseBooked() {
        booked++;
    }

    public void increaseStolen() {
        stolen++;
    }

    public void increaseRecharging() {
        recharging++;
    }

    public int getStolen() {
        return stolen;
    }

    public int getRecharging() {
        return recharging;
    }

    public int getBooked() {
        return booked;
    }

    public int getBroken() {
        return broken;
    }

    public int getAvailable() {
        return available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleCounter that = (VehicleCounter) o;

        if (getAvailable() != that.getAvailable()) return false;
        if (getBroken() != that.getBroken()) return false;
        if (getBooked() != that.getBooked()) return false;
        if (getRecharging() != that.getRecharging()) return false;
        return getStolen() == that.getStolen();
    }

    @Override
    public int hashCode() {
        int result = getAvailable();
        result = 31 * result + getBroken();
        result = 31 * result + getBooked();
        result = 31 * result + getRecharging();
        result = 31 * result + getStolen();
        return result;
    }

    @Override
    public String toString() {
        return "VehicleCounter{" +
                "available=" + available +
                ", broken=" + broken +
                ", booked=" + booked +
                ", recharging=" + recharging +
                ", stolen=" + stolen +
                '}';
    }
}
