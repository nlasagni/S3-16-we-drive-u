package com.wedriveu.backoffice.controller;

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
        //Application.launch(MapViewerJavaFX.class, generateMapParameters(list));
        MapViewerJavaFX.run(generateMapParameters(list));
    }

    private static String generateMapParameters(List<Booking> bookings) {
        String parameters = "";
        for (Booking book: bookings) {
            parameters = parameters + "new google.maps.Marker({" +
                    "position: {lat: "
                    +book.getUserPosition().getLatitude() +
                    ", lng:" +
                    +book.getUserPosition().getLongitude() +
                    "}," +
                    "map: map," +
                    "icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png'" +
                    "});" +
                    "new google.maps.Marker({" +
                    "position: {lat: " +
                    book.getDestinationPosition().getLatitude() +
                    ", lng: " +
                    book.getDestinationPosition().getLongitude() +
                    "}," +
                    "map: map," +
                    "icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'" +
                    "});";
        }
        return parameters;
    }

}
