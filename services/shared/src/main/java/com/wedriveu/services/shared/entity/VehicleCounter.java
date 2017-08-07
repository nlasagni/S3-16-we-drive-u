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


    public VehicleCounter() {
        available = 0;
        broken = 0;
        booked = 0;
        recharging = 0;
        stolen = 0;
    }

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

    public void decreaseAvailable() {
        available--;
    }

    public void decreaseBroken() {
        broken--;
    }

    public void decreaseBooked() {
        booked--;
    }

    public void decreaseStolen(){
        stolen--;
    }

    public void decreaseRecharging() {
        recharging--;
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

}
