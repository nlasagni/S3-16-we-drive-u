package com.wedriveu.services.shared.utilities;

import com.wedriveu.shared.entity.Position;
import com.wedriveu.shared.util.Constants;

import static com.wedriveu.shared.util.Constants.Position.EARTH_RADIUS;
import static com.wedriveu.shared.util.Constants.Position.RANGE;

/**
 * @author Marco Baldassarri
 * @since 04/08/2017.
 */
public class PositionUtils {



    public static double getDistanceInKm(Position from, Position to) {
        double earthRadius = EARTH_RADIUS;
        return earthRadius * Math.acos(Math.sin(from.getLatitude()) * Math.sin(to.getLatitude())
                + Math.cos(from.getLatitude()) * Math.cos(to.getLatitude())
                * Math.cos(from.getLatitude() - to.getLongitude()));
    }

    public static boolean isInRange(Position userPosition, Position vehiclePosition) {
        return userPosition.getEuclideanDistance(vehiclePosition) < RANGE;
    }

}
