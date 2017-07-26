package com.wedriveu.services.vehicle.finder.control;

public class CounterVehiclesEligibles {
    private int called;
    private int finished;

    public CounterVehiclesEligibles() {
        called = 0;
        finished = 0;
    }

    synchronized void addCalled() {
        called++;
    }

    synchronized void addFinished() {
        finished++;
    }

    synchronized boolean isFinished() {
        return called == finished;
    }

}
