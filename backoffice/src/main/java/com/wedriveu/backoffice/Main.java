package com.wedriveu.backoffice;

import com.wedriveu.backoffice.controller.BackofficeController;
import com.wedriveu.backoffice.view.MapViewerJavaFX;
import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import io.vertx.core.Vertx;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new BackofficeController(vertx), completed -> {
            if (completed.succeeded()) {
                Log.info("correctly started Analytics service");
            } else {
                Log.error("main analytics service", completed.cause().getLocalizedMessage(), completed.cause());
            }
        });
        List<Booking> bookings = new ArrayList<>();
        bookings.add(
                new Booking(1,
                        new Date(2017,11,10),
                        "pippo",
                        "veicolo1",
                        new Position(44,12),
                        new Position(44.9,12),
                        Booking.STATUS_COMPLETED));
        bookings.add(
                new Booking(2,
                        new Date(2017,11,11),
                        "pippo",
                        "veicolo1",
                        new Position(45,12),
                        new Position(46,12),
                        Booking.STATUS_COMPLETED));
        bookings.add(
                new Booking(3,
                        new Date(2017,11,12),
                        "pippo",
                        "veicolo1",
                        new Position(44,13),
                        new Position(47,12),
                        Booking.STATUS_COMPLETED));
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
                    book.getUserPosition().getLongitude() +
                    "}," +
                    "map: map," +
                    "icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png'" +
                    "});";
        }
        Application.launch(MapViewerJavaFX.class, parameters);
    }
}
