package com.wedriveu.shared.util;

import static com.wedriveu.shared.util.Constants.Position.EARTH_RADIUS;
import static com.wedriveu.shared.util.Constants.Position.RANGE;
import static com.wedriveu.shared.util.Constants.Position.SUBSTITUTION_RANGE;

/**
 * @author Marco Baldassarri
 * @since 04/08/2017.
 */
public class PositionUtils {

    private static final int FLAT_ANGLE = 180;

    public static double getDistanceInKm(Position from, Position to) {
        double fromLatitudeInRad = toRadiants(from.getLatitude());
        double fromLongitudeInRad = toRadiants(from.getLongitude());
        double toLatitudeInRad = toRadiants(to.getLatitude());
        double toLongitudeInRad = toRadiants(to.getLongitude());
        return EARTH_RADIUS * Math.acos(Math.sin(fromLatitudeInRad) * Math.sin(toLatitudeInRad)
                + Math.cos(fromLatitudeInRad) * Math.cos(toLatitudeInRad)
                * Math.cos(fromLongitudeInRad - toLongitudeInRad));
    }

    public static boolean isInRange(Position userPosition, Position vehiclePosition, double range) {
        return getDistanceInKm(userPosition, vehiclePosition) < range;
    }

    private static double toRadiants(double coordinate) {
        return (coordinate * Math.PI) / FLAT_ANGLE;
    }

}
