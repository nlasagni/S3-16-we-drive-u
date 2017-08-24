package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.booking.BookingRequestReceiverConsumer;
import com.wedriveu.backoffice.util.ConstantsBackoffice;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.BookingListRequest;
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
public class BackofficeBookingsRequestPublisherTest {
    private List<Future> futures;
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        futures = new ArrayList<>();
        Future retrieveFuture = Future.future();
        vertx.deployVerticle(new BackofficeBookingsRequestPublisher(ConstantsBackoffice.TEST_BACKOFFICE_ID), retrieveFuture.completer());
        futures.add(retrieveFuture);

        Future generatorRequestHandlerFuture = Future.future();
        vertx.deployVerticle(new BookingRequestReceiverConsumer(), generatorRequestHandlerFuture.completer());
        futures.add(generatorRequestHandlerFuture);
    }

    @Test
    public void testVehicleResponsePublisher(TestContext context) {
        Async async = context.async();
        CompositeFuture.all(futures).setHandler(completed -> {
            BookingListRequest bookingListRequestLocal = new BookingListRequest();
            bookingListRequestLocal.setBackofficeId(ConstantsBackoffice.TEST_BACKOFFICE_ID);
            JsonObject dataToUser = new JsonObject();
            dataToUser.put(Constants.EventBus.BODY, "requesting");
            vertx.eventBus().send(ConstantsBackoffice.EventBus.BACKOFFICE_BOOKING_LIST_REQUEST_CONTROLLER, dataToUser);
            vertx.eventBus().consumer(ConstantsBackoffice.EventBus.BACKOFFICE_BOOKING_LIST_REQUEST_TEST, res -> {
                BookingListRequest bookingListRequestReceived =
                        VertxJsonMapper.mapFromBodyTo((JsonObject) res.body(), BookingListRequest.class);
                assertTrue(bookingListRequestLocal.getBackofficeId().equals(bookingListRequestReceived.getBackofficeId()));
                async.complete();
            });
        });
        async.awaitSuccess();
    }
}