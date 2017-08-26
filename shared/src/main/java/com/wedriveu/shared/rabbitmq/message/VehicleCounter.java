package com.wedriveu.shared.rabbitmq.message;

/**
 * a class for storing a vehicle counter for all the vehicles registered to the service
 *
 * @author Stefano Bernagozzi
 */

public class VehicleCounter {

    private int available;
    private int brokenStolen;
    private int booked;
    private int recharging;
    private int networkIssues;

    /**
     * increases of 1 the number of vehicles availables
     */
    public void increaseAvailable() {
        available++;
    }

    /**
     * increases of 1 the number of vehicles broken or stolen
     */
    public void increaseBroken() {
        brokenStolen++;
    }

    /**
     * increases of 1 the number of vehicles booked
     */
    public void increaseBooked() {
        booked++;
    }

    /**
     * increases of 1 the number of vehicles with network issues
     */
    public void increaseNetworkIssues() {
        networkIssues++;
    }

    /**
     * increases of 1 the number of vehicles recharging
     */
    public void increaseRecharging() {
        recharging++;
    }

    /**
     * @return the number of vehicles with network issues
     */
    public int getNetworkIssues() {
        return networkIssues;
    }

    /**
     * @return the number of vehicles recharging
     */
    public int getRecharging() {
        return recharging;
    }
    /**
     * @return the number of vehicles booked
     */
    public int getBooked() {
        return booked;
    }
    /**
     * @return the number of vehicles broken or stolen
     */
    public int getBrokenStolen() {
        return brokenStolen;
    }
    /**
     * @return the number of vehicles available
     */
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
