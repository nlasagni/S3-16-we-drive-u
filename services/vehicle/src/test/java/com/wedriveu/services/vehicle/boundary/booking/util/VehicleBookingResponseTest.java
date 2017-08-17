package com.wedriveu.services.vehicle.boundary.booking.util;

import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.booking.entity.BookingRequest;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryMini;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.rabbitmq.message.VehicleReservationRequest;
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
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Please Run BookingTest.java before
 */
@RunWith(VertxUnitRunner.class)
public class VehicleBookingResponseTest extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = VehicleBookingResponseTest.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.booking.test.consumer";
    private static final int ASYNC_COUNT = 3;
    private static final int SPEED = 50;
    private static final boolean BOOKED = false;
    private Async async;
    private Vertx vertx;
    private final BookVehicleRequest bookingRequest;
    private JsonObject responseJson;

    public VehicleBookingResponseTest() {

        super(QUEUE, VEHICLE, BOOK_REQUEST, BOOK_RESPONSE, EVENT_BUS_ADDRESS);

        bookingRequest = new BookingRequest().getBookingVehicleRequest();
    }

    @Before
    @SuppressWarnings("Duplicates")
    public void setUp(TestContext context) throws Exception {
        async = context.async(ASYNC_COUNT);
        vertx = Vertx.vertx();
        super.setup(vertx, completed -> {
            async.countDown();
            Vehicle vehicle = new VehicleFactoryMini().getVehicle();
            super.declareQueueAndBind(vehicle.getLicensePlate(), context, declared -> {
                context.assertTrue(declared.succeeded());
                async.countDown();
                deployVerticles(context);
            });
        });
        async.awaitSuccess();
    }

    @SuppressWarnings("Duplicates")
    private void deployVerticles(TestContext context) {
        vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, completed -> {
            vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                async.complete();
            });
            vertx.eventBus().send(Messages.VehicleStore.CLEAR_VEHICLES, null);
        });
        vertx.deployVerticle(new BootVerticle(), context.asyncAssertSuccess(onDeploy -> {
            vertx.eventBus().send(Messages.VehicleService.BOOT, null);
        }));
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        super.stop(context);
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        this.responseJson = responseJson;
        checkResponseJson(context);
        publishResponseToVehicleService(context);
    }

    @Test
    public void checkResponseJson(TestContext context) {
        VehicleReservationRequest reservationRequest = responseJson.mapTo(VehicleReservationRequest.class);
        context.assertNotNull(responseJson);
        context.assertNotNull(reservationRequest.getUsername());
        context.assertEquals(reservationRequest.getUsername(), bookingRequest.getUsername());
        assertThat(reservationRequest, instanceOf(VehicleReservationRequest.class));
    }


    private void publishResponseToVehicleService(TestContext context){
        super.publishMessage(true, context, getJson());
    }

    @Override
    protected JsonObject getJson() {
        BookVehicleResponse bookVehicleResponse = new BookVehicleResponse();
        bookVehicleResponse.setBooked(BOOKED);
        bookVehicleResponse.setSpeed(SPEED);
        return VertxJsonMapper.mapInBodyFrom(bookingRequest);
    }

}