package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.nearest.entity.UserDataFactoryA;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.services.vehicle.rabbitmq.UserRequest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;
import static com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(VertxUnitRunner.class)
public class VehicleNearestVerticleTest extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = VehicleNearestVerticleTest.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.nearest";
    private static final int ASYNC_COUNT = 3;
    private Async async;
    private Vertx vertx;

    public VehicleNearestVerticleTest() {
        super(QUEUE, VEHICLE, VEHICLE_REQUEST, VEHICLE_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        async = context.async(ASYNC_COUNT);
        vertx = Vertx.vertx();
        super.setup(vertx, completed -> {
            async.countDown();
            String username = new UserDataFactoryA().getUserData().getUsername();
            super.declareQueueAndBind(username, context, declared -> {
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
            async.complete();
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
        super.publishMessage(false, context, getJson());
    }

    @Override
    protected void checkResponse(JsonObject responseJson) {
        Vehicle responseVehicle = responseJson.mapTo(Vehicle.class);
        assertThat(responseVehicle, instanceOf(Vehicle.class));
    }

    @Override
    protected JsonObject getJson() {
        UserRequest userDataA = new UserDataFactoryA().getUserData();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, JsonObject.mapFrom(userDataA).toString());
        return jsonObject;
    }

}