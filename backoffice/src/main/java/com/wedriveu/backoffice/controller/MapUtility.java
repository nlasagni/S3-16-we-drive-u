package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.backoffice.view.MapViewerJavaFX;
import com.wedriveu.services.shared.model.Booking;

import java.util.List;

/**
 * class for generating a browser with google maps in java
 *
 * @author Stefano Bernagozzi
 */
public class MapUtility {

    /**
     * launch a browser with a map inside with pins in the starting and ending position of the bookings
     *
     * @param list a list of booking for putting the pins on the map
     */
    public static void generateMap(List<Booking> list) {
        MapViewerJavaFX.run(generateMapParameters(list));
    }

    private static String generateMapParameters(List<Booking> bookings) {
        StringBuilder builder = new StringBuilder();
        for (Booking book: bookings) {
            builder.append(
                    String.format(ConstantsBackOffice.WebPage.JAVASCRIPT_MARKER_CODE_STARTING_POSITION,
                        book.getUserPosition().getLatitude(),
                        book.getUserPosition().getLongitude()));
            builder.append(
                    String.format(ConstantsBackOffice.WebPage.JAVASCRIPT_MARKER_CODE_DESTINATION_POSITION,
                            book.getDestinationPosition().getLatitude(),
                            book.getDestinationPosition().getLongitude()));
        }
        return builder.toString();
    }

}
