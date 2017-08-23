package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.booking.entity.BookingRequest;
import com.wedriveu.services.vehicle.boundary.booking.mock.VehicleMockVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.RegisterVehicleTestMini;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryMini;
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
import org.junit.runner.JUnitCore;
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
    private static final String LICENSE_PLATE = new VehicleFactoryMini().getVehicle().getLicensePlate();
    private static final int ASYNC_COUNT = 3;
    private VehicleMockVerticle mockVerticle;
    private Async async;
    private Vertx vertx;

    public BookingTest() {
        super(QUEUE, VEHICLE, VEHICLE_SERVICE_BOOK_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    @SuppressWarnings("Duplicates")
    public void setUp(TestContext context) throws Exception {
        async = context.async(ASYNC_COUNT);
        vertx = Vertx.vertx();
        mockVerticle = new VehicleMockVerticle(LICENSE_PLATE);
        super.setup(vertx, handler -> {
            vertx.deployVerticle(mockVerticle, onMockDeploy -> {
                super.declareQueueAndBind("", context, declared -> {
                    context.assertTrue(declared.succeeded());
                    async.complete();
                });
            });
        });
        async.awaitSuccess();
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        super.stop(context);
        vertx.undeploy(mockVerticle.deploymentID());
    }

    @Test
    public void publishMessage(TestContext context) throws Exception {
        JUnitCore.runClasses(RegisterVehicleTestMini.class);
        publishMessage(context, false, VEHICLE, VEHICLE_SERVICE_BOOK_REQUEST, createRequest());
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
        bookingRequest.setLicencePlate(LICENSE_PLATE);
        return VertxJsonMapper.mapInBodyFrom(bookingRequest);
    }

}