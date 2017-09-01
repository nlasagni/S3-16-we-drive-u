package com.wedriveu.services.vehicle.boundary.vehiclearrived;

import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.util.BaseInteractionClient;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingRequest;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nicola Lasagni on 23/08/2017.
 */
@RunWith(VertxUnitRunner.class)
public class CompleteBookingVerticleTest extends BaseInteractionClient {

    private static final String QUEUE = CompleteBookingVerticleTest.class.getCanonicalName() + ".Queue";
    private static final String EVENT_BUS_ADDRESS =
            CompleteBookingVerticleTest.class.getCanonicalName() + ".EventBus";
    private static final String USERNAME = "username";
    private static final String LICENSE_PLATE = "licensePlate";

    private Vertx vertx;
    private BootVerticle bootVerticle;

    public CompleteBookingVerticleTest() {
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
        vertx = Vertx.vertx();
        bootVerticle = new BootVerticle();
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
            vertx.deployVerticle(bootVerticle, context.asyncAssertSuccess(onDeploy -> {
                vertx.eventBus().send(Messages.VehicleService.BOOT, null);
            }));
        });
        async.awaitSuccess();
    }

    @Test
    public void vehicleArrived(TestContext context) {
        super.publishMessageAndWaitResponse(context,
                Constants.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_ARRIVED,
                createRequest());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        CompleteBookingRequest request = responseJson.mapTo(CompleteBookingRequest.class);
        context.assertTrue(request != null);
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