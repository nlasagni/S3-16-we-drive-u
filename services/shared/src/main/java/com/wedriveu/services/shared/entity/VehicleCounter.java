package com.wedriveu.services.shared.entity;

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

    public void increaseStolen(){
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

    public String toString() {
        return "available: " + available +
                " booked: " + booked +
                " broken: " + broken +
                " recharging: " + recharging +
                " stolen: " + stolen;
    }

    public boolean equals(VehicleCounter vehicleCounter) {
        return  this.available == vehicleCounter.getAvailable() &&
                this.booked == vehicleCounter.getBooked() &&
                this.broken == vehicleCounter.getBroken() &&
                this.recharging == vehicleCounter.getRecharging() &&
                this.stolen == vehicleCounter.getStolen();
    }

}
