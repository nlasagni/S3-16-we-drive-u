package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.nearest.entity.UserDataFactoryA;
import com.wedriveu.services.vehicle.boundary.util.BaseInteractionClient;
import com.wedriveu.services.vehicle.entity.UserRequest;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;
import static com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Nearest request simulator, it acts as the Android client and sends the request to the VehicleService given
 * a proper user address and destination address.
 */
@RunWith(VertxUnitRunner.class)
public class VehicleNearestVerticleTest extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = VehicleNearestVerticleTest.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.nearest";
    private static final int ASYNC_COUNT = 3;

    private Vertx vertx;
    private BootVerticle bootVerticle;
    private Async async;

    public VehicleNearestVerticleTest() {
        super(QUEUE, VEHICLE, VEHICLE_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        async = context.async(ASYNC_COUNT);
        vertx = Vertx.vertx();
        bootVerticle = new BootVerticle();
        super.setup(vertx, completed -> {
            vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, onCompleted -> {
                vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                    async.countDown();
                    String username = new UserDataFactoryA().getUserData().getUsername();
                    super.declareQueueAndBind(username, context, declared -> {
                        context.assertTrue(declared.succeeded());
                        async.complete();
                    });
                });
                vertx.eventBus().send(Messages.VehicleStore.CLEAR_VEHICLES, null);
            });
            async.countDown();
            vertx.deployVerticle(bootVerticle, context.asyncAssertSuccess(onDeploy -> {
                vertx.eventBus().send(Messages.VehicleService.BOOT, null);
            }));
        });
        async.awaitSuccess();
    }

    @Test
    public void publishMessage(TestContext context) throws Exception {
        super.publishMessageAndWaitResponse(context, VEHICLE, VEHICLE_REQUEST, getJson());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        VehicleResponse responseVehicle = responseJson.mapTo(VehicleResponse.class);
        assertThat(responseVehicle, instanceOf(VehicleResponse.class));
    }

    private JsonObject getJson() {
        UserRequest userDataA = new UserDataFactoryA().getUserData();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, JsonObject.mapFrom(userDataA).toString());
        return jsonObject;
    }

}