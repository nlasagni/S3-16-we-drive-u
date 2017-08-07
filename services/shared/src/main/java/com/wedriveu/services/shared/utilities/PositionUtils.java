package com.wedriveu.services.shared.utilities;

/**
 * Created by Marco on 04/08/2017.
 */
public class PositionUtils {


    public static double getDistanceInKm(Position from, Position to) {
        double earthRadius = 6372.795477598;
        return earthRadius * Math.acos(Math.sin(from.getLatitude()) * Math.sin(to.getLatitude())
                + Math.cos(from.getLatitude()) * Math.cos(to.getLatitude())
                * Math.cos(from.getLatitude() - to.getLongitude()));
    }

    public static boolean isInRange(Position userPosition, Position vehiclePosition) {
        return userPosition.getEuclideanDistance(vehiclePosition) < Constants.RANGE;
    }

}
