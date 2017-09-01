package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.booking.entity.BookingRequest;
import com.wedriveu.services.vehicle.boundary.util.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.util.mock.VehicleBookingMockVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryMini;
import com.wedriveu.services.vehicle.entity.VehicleStore;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
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
 */
@RunWith(VertxUnitRunner.class)
public class BookingTest extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = BookingTest.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.booking.test";
    private static final String USERNAME = "BookingTestUser";

    private Vertx vertx;
    private BootVerticle bootVerticle;
    private Vehicle vehicle;
    private Async async;
    private VehicleStore vehicleStore;

    public BookingTest() {
        super(QUEUE, VEHICLE, VEHICLE_SERVICE_BOOK_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        async = context.async();
        vertx = Vertx.vertx();
        bootVerticle = new BootVerticle();
        vehicleStore = new VehicleStoreImpl();
        vehicle = new VehicleFactoryMini().getVehicle();
        VehicleBookingMockVerticle mockVerticle =
                new VehicleBookingMockVerticle(USERNAME, vehicle.getLicensePlate());
        vertx.deployVerticle(mockVerticle, onMockDeploy -> {
            super.setup(vertx, completed -> {
                vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, onCompleted -> {
                    vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                        vehicleStore.addVehicle(vehicle);
                        super.declareQueueAndBind(vehicle.getLicensePlate(), context, declared -> {
                            context.assertTrue(declared.succeeded());
                            async.complete();
                        });
                    });
                    vertx.eventBus().send(Messages.VehicleStore.CLEAR_VEHICLES, null);
                });
                vertx.deployVerticle(bootVerticle, context.asyncAssertSuccess(onDeploy -> {
                    vertx.eventBus().send(Messages.VehicleService.BOOT, null);
                }));
            });
        });
        async.awaitSuccess();
    }

    @Test
    public void publishMessage(TestContext context) throws Exception {
        publishMessageAndWaitResponse(context, VEHICLE, VEHICLE_SERVICE_BOOK_REQUEST, createRequest());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        BookVehicleResponse bookVehicleResponse = responseJson.mapTo(BookVehicleResponse.class);
        context.assertNotNull(responseJson);
        context.assertNotNull(bookVehicleResponse);
        assertThat(bookVehicleResponse, instanceOf(BookVehicleResponse.class));
    }

    private JsonObject createRequest() {
        BookVehicleRequest bookingRequest = new BookingRequest().getBookingVehicleRequest();
        bookingRequest.setUsername(USERNAME);
        bookingRequest.setLicencePlate(vehicle.getLicensePlate());
        return VertxJsonMapper.mapInBodyFrom(bookingRequest);
    }

}