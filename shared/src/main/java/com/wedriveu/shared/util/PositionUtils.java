package com.wedriveu.shared.util;

import static com.wedriveu.shared.util.Constants.Position.EARTH_RADIUS;
import static com.wedriveu.shared.util.Constants.Position.RANGE;

/**
 * class for handling distance of positions and convert from decimal to radiants
 *
 * @author Marco Baldassarri
 * @since 04/08/2017.
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
     * @param userPosition the user position
     * @param vehiclePosition the vehicle position
     * @return true if the vehicle is in range of 20 km from the user, false otherwise
     */
    public static boolean isInRange(Position userPosition, Position vehiclePosition) {
        return getDistanceInKm(userPosition, vehiclePosition) < RANGE;
    }

    /**
     * @param coordinate a coordinate expressed in degrees
     * @return the coordinate expressed in radiants
     */
    private static double toRadiants(double coordinate) {
        return (coordinate * Math.PI) / FLAT_ANGLE;
    }

}
