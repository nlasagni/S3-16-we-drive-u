package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.booking.BookingListGenerator;
import com.wedriveu.backoffice.booking.BookingResponseGeneratorPublisher;
import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Stefano Bernagozzi
 */
@RunWith(VertxUnitRunner.class)
public class BackOfficeBookingsResponseConsumerTest {
    private List<Future> futures;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new BackOfficeBookingsResponseConsumer(ConstantsBackOffice.TEST_BACKOFFICE_ID), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new BookingResponseGeneratorPublisher(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);
    }

    @Test
    public void testBookingResponsePublisher(TestContext context) {
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            JsonObject dataToUser = new JsonObject();
            dataToUser.put(Constants.EventBus.BODY, "requesting");
            vertx.eventBus().send(ConstantsBackOffice.EventBus.BACKOFFICE_BOOKING_LIST_RESPONSE_GENERATOR_START_TEST, dataToUser);
            vertx.eventBus().consumer(ConstantsBackOffice.EventBus.BACKOFFICE_CONTROLLER_BOOKINGS, res -> {
                try {
                    List<Booking> bookingList =
                            VertxJsonMapper.mapFromBodyToList((JsonObject) res.body(), Booking.class);
                    assertTrue(bookingList.equals(BookingListGenerator.getBookingsFromBookingService()));
                    async.complete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        async.awaitSuccess();
    }
}