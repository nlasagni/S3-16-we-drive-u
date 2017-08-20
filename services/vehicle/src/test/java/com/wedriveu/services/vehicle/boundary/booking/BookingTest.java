package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.booking.entity.BookingRequest;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_SERVICE_BOOK_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_SERVICE_BOOK_RESPONSE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * BookingService simulator, it sends the VehicleService the booking data and starts
 * waiting for a proper booking response.
 * During this test, {@link com.wedriveu.services.vehicle.boundary.booking.util.VehicleBookingResponseTest} has to be
 * up and running in order to interact with a simulated Vehicle during the booking process.
 */
@RunWith(VertxUnitRunner.class)
public class BookingTest extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = BookingTest.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.booking.test";
    private static final int ASYNC_COUNT = 3;
    private Async async;
    private Vertx vertx;

    public BookingTest() {
        super(QUEUE, VEHICLE, VEHICLE_SERVICE_BOOK_REQUEST, VEHICLE_SERVICE_BOOK_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    @SuppressWarnings("Duplicates")
    public void setUp(TestContext context) throws Exception {
        async = context.async(ASYNC_COUNT);
        vertx = Vertx.vertx();
        super.setup(vertx, completed -> {
            vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, onCompleted -> {
                vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                    async.countDown();
                    super.declareQueueAndBind("", context, declared -> {
                        context.assertTrue(declared.succeeded());
                        async.complete();
                    });
                });
                vertx.eventBus().send(Messages.VehicleStore.CLEAR_VEHICLES, null);
            });
            async.countDown();
            vertx.deployVerticle(new BootVerticle(), context.asyncAssertSuccess(onDeploy -> {
                vertx.eventBus().send(Messages.VehicleService.BOOT, null);
            }));
        });
        async.awaitSuccess();
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        super.stop(context);
    }

    @Test
    public void publishMessage(TestContext context) throws Exception {
        super.publishMessage(false, context, getJson());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        BookVehicleResponse bookVehicleResponse = responseJson.mapTo(BookVehicleResponse.class);
        context.assertNotNull(responseJson);
        context.assertNotNull(bookVehicleResponse);
        assertThat(bookVehicleResponse, instanceOf(BookVehicleResponse.class));
    }

    @Override
    protected JsonObject getJson() {
        BookVehicleRequest bookingRequest = new BookingRequest().getBookingVehicleRequest();
        return VertxJsonMapper.mapInBodyFrom(bookingRequest);
    }

}