package com.wedriveu.services.vehicle.util;

/**
 * Utility class to manage time operations.
 *
 * @author Nicola Lasagni on 26/08/2017.
 */
public class Time {

    private static final double HOUR_IN_MILLISECONDS = 3600000.0;

    /**
     * Calculates the drive time in milliseconds given a distance and a speed.
     * @param distance The distance in kilometers
     * @param speed The speed in kph
     * @return The drive time in milliseconds
     */
    public static long getDriveTimeInMilliseconds(double distance, double speed) {
        return (long) ((distance / speed) * HOUR_IN_MILLISECONDS);
    }

}
