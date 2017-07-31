package com.wedriveu.services.vehicle.finder.control;

import com.wedriveu.services.shared.utilities.Log;

public class CounterVehiclesEligibles {
    private int called;
    private int finished;

    public CounterVehiclesEligibles() {
        called = 0;
        finished = 0;
    }

    synchronized void addCalled() {
        called++;
        Log.log("Called: "+called);
    }

    synchronized void addFinished() {
        finished++;
        Log.log("Finished: "+finished);
    }

    synchronized boolean isFinished() {
        return called == finished;
    }

}
