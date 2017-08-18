package com.wedriveu.services.vehicle.boundary.vehicleregister;

<<<<<<< HEAD
import com.wedriveu.shared.rabbitmq.message.Vehicle;
=======
import com.wedriveu.services.shared.model.Vehicle;
>>>>>>> WDU_75_Booking_Service_RabbitMQ_Setup
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryMini;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.REGISTER_RESULT;
import static com.wedriveu.shared.util.Constants.EventBus.BODY;
import static com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(VertxUnitRunner.class)
public class RegisterVehicleTestMini extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = RegisterVehicleTestMini.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.mini";
    private static final int ASYNC_COUNT = 5;
    private Async async;
    private Vertx vertx;

    public RegisterVehicleTestMini() {
        super(QUEUE, VEHICLE, REGISTER_REQUEST, REGISTER_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        async = context.async(ASYNC_COUNT);
        vertx = Vertx.vertx();
        super.setup(vertx, completed -> {
            async.countDown();
            String licencePlate = new VehicleFactoryMini().getVehicle().getLicensePlate();
            super.declareQueueAndBind(licencePlate, context, declared -> {
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

    @Test
    public void publishMessage(TestContext context) throws Exception {
        super.publishMessage(true, context, getJson());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        assertThat(responseJson.getBoolean(REGISTER_RESULT), instanceOf(Boolean.class));
    }

    @Override
    protected JsonObject getJson() {
        Vehicle vehicle = new VehicleFactoryMini().getVehicle();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, JsonObject.mapFrom(vehicle).toString());
        return jsonObject;
    }

}