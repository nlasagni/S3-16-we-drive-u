package com.wedriveu.services.vehicle.util;

/**
 * @author Nicola Lasagni on 26/08/2017.
 */
public class Time {

    private static final long HOUR_IN_MILLISECONDS = 3600000;

    public static long getTimeInMilliseconds(double distance, double speed) {
        double hourTime = (distance / speed) * HOUR_IN_MILLISECONDS;
        return (long) hourTime;
    }

}
