package com.wedriveu.shared.util;

import static com.wedriveu.shared.util.Constants.Position.EARTH_RADIUS;

/**
 * class for handling distance of positions and convert from decimal to radiants
 *
 * @author Marco Baldassarri
 */
public class PositionUtils {

    private static final int FLAT_ANGLE = 180;

    /**
     * returns the distance between two points
     * @param from the starting point
     * @param to the ending point
     * @return the distance between the two points expressed in kilometers
     */
    public static double getDistanceInKm(Position from, Position to) {
        double fromLatitudeInRad = toRadiants(from.getLatitude());
        double fromLongitudeInRad = toRadiants(from.getLongitude());
        double toLatitudeInRad = toRadiants(to.getLatitude());
        double toLongitudeInRad = toRadiants(to.getLongitude());
        return EARTH_RADIUS * Math.acos(Math.sin(fromLatitudeInRad) * Math.sin(toLatitudeInRad)
                + Math.cos(fromLatitudeInRad) * Math.cos(toLatitudeInRad)
                * Math.cos(fromLongitudeInRad - toLongitudeInRad));
    }

    /**
     *
     * @param sourcePosition the source position
     * @param vehiclePosition the vehicle position
     * @param minRange the minimum range
     * @param maxRange the maximum range
     * @return true if the vehicle is inside the specified {@code minRange} and {@code maxRange}
     *         of a {@code sourcePosition}.
     */
    public static boolean isInRange(Position sourcePosition,
                                    Position vehiclePosition,
                                    double minRange,
                                    double maxRange) {
        double distance = getDistanceInKm(sourcePosition, vehiclePosition);
        return distance > minRange && distance <= maxRange;
    }

    /**
     * @param coordinate a coordinate expressed in degrees
     * @return the coordinate expressed in radiants
     */
    private static double toRadiants(double coordinate) {
        return (coordinate * Math.PI) / FLAT_ANGLE;
    }

}
