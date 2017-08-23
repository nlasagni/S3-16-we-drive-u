package com.wedriveu.services.vehicle.boundary.vehiclearrived;

import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.BaseInteractionClient;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingRequest;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nicola Lasagni on 23/08/2017.
 */
@RunWith(VertxUnitRunner.class)
public class VehicleArrivedVerticleTest extends BaseInteractionClient {

    private static final String QUEUE = VehicleArrivedVerticleTest.class.getCanonicalName() + ".Queue";
    private static final String EVENT_BUS_ADDRESS =
            VehicleArrivedVerticleTest.class.getCanonicalName() + ".EventBus";
    private static final String USERNAME = "username";
    private static final String LICENSE_PLATE = "licensePlate";

    public VehicleArrivedVerticleTest() {
        super(
                QUEUE,
                Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.COMPLETE_BOOKING_REQUEST,
                EVENT_BUS_ADDRESS
        );
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        Async async = context.async();
        Vertx vertx = Vertx.vertx();
        super.setup(vertx, completed -> {
            vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, onCompleted -> {
                vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                    super.declareQueueAndBind(null, context, declared -> {
                        context.assertTrue(declared.succeeded());
                        async.complete();
                    });
                });
                vertx.eventBus().send(Messages.VehicleStore.CLEAR_VEHICLES, null);
            });
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
    public void vehicleArrived(TestContext context) {
        super.publishMessage(context,
                true,
                Constants.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_ARRIVED,
                createRequest());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        CompleteBookingRequest request = responseJson.mapTo(CompleteBookingRequest.class);
        context.assertTrue(USERNAME.equals(request.getUsername()) &&
                LICENSE_PLATE.equals(request.getLicensePlate()));
    }

    private JsonObject createRequest() {
        ArrivedNotify notifyToSend = new ArrivedNotify();
        notifyToSend.setLicense(LICENSE_PLATE);
        notifyToSend.setUsername(USERNAME);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(notifyToSend).toString());
        return jsonObject;
    }
}