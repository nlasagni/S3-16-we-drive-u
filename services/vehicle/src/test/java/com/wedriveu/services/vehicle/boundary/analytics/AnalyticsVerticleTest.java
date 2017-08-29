package com.wedriveu.services.vehicle.boundary.analytics;

import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.util.BaseInteractionClient;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
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
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLES_RESPONSE_ALL;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLE_REQUEST_ALL;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(VertxUnitRunner.class)
public class AnalyticsVerticleTest extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = AnalyticsVerticleTest.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.analytics.test";

    private Vertx vertx;
    private BootVerticle bootVerticle;
    private Async async;

    public AnalyticsVerticleTest() {
        super(QUEUE, VEHICLE, ANALYTICS_VEHICLES_RESPONSE_ALL, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        bootVerticle = new BootVerticle();
        async = context.async();
        super.setup(vertx, completed -> {
            vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, onCompleted -> {
                vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                    super.declareQueueAndBind("", context, declared -> {
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
    public void publishMessage(TestContext context) throws Exception {
        super.publishMessageAndWaitResponse(context, VEHICLE, ANALYTICS_VEHICLE_REQUEST_ALL, getJson());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        AnalyticsVehicleList vehicles = responseJson.mapTo(AnalyticsVehicleList.class);
        context.assertNotNull(responseJson);
        context.assertNotNull(vehicles.getVehicleList());
        vehicles.getVehicleList().forEach(vehicle -> {
            context.assertNotNull(vehicle.getLicensePlate());
            context.assertNotNull(vehicle.getPosition());
        });
        assertThat(vehicles, instanceOf(AnalyticsVehicleList.class));
    }

    private JsonObject getJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, "");
        return jsonObject;
    }

}