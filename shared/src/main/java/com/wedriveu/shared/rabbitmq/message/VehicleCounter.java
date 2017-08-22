package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Stefano Bernagozzi
 */

public class VehicleCounter {

    private int available;
    private int brokenStolen;
    private int booked;
    private int recharging;
    private int networkIssues;

    public void increaseAvailable() {
        available++;
    }

    public void increaseBroken() {
        brokenStolen++;
    }

    public void increaseBooked() {
        booked++;
    }

    public void increaseNetworkIssues() {
        networkIssues++;
    }

    public void increaseRecharging() {
        recharging++;
    }

    public int getNetworkIssues() {
        return networkIssues;
    }

    public int getRecharging() {
        return recharging;
    }

    public int getBooked() {
        return booked;
    }

    public int getBrokenStolen() {
        return brokenStolen;
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
        if (getBrokenStolen() != that.getBrokenStolen()) return false;
        if (getBooked() != that.getBooked()) return false;
        if (getRecharging() != that.getRecharging()) return false;
        return getNetworkIssues() == that.getNetworkIssues();
    }

    @Override
    public int hashCode() {
        int result = getAvailable();
        result = 31 * result + getBrokenStolen();
        result = 31 * result + getBooked();
        result = 31 * result + getRecharging();
        result = 31 * result + getNetworkIssues();
        return result;
    }

    @Override
    public String toString() {
        return "VehicleCounter{" +
                "available=" + available +
                ", brokenStolen=" + brokenStolen +
                ", booked=" + booked +
                ", recharging=" + recharging +
                ", networkIssues=" + networkIssues +
                '}';
    }
}
