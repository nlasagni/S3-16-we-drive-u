package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.vehicle.boundary.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryFiat;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static com.wedriveu.services.shared.utilities.Constants.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(VertxUnitRunner.class)
public class RegisterVehicleTestFiat extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = RegisterVehicleTestFiat.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.fiat";
    private static final int ASYNC_COUNT = 5;
    private Async async;
    private Vertx vertx;

    public RegisterVehicleTestFiat() {
        super(QUEUE, VEHICLE_SERVICE_EXCHANGE, ROUTING_KEY_REGISTER_VEHICLE_REQUEST,
                ROUTING_KEY_REGISTER_VEHICLE_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        async = context.async(ASYNC_COUNT);
        vertx = Vertx.vertx();
        super.setup(vertx, completed -> {
            async.countDown();
            String licencePlate = new VehicleFactoryFiat().getVehicle().getCarLicencePlate();
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
        vertx.deployVerticle(new RegisterConsumerVerticle(), context.asyncAssertSuccess(onDeployConsumer -> {
            async.countDown();
            vertx.deployVerticle(new RegisterPublisherVerticle(), context.asyncAssertSuccess(onDeployPublisher -> {
                async.countDown();
                vertx.deployVerticle(new VehicleStoreImpl(), context.asyncAssertSuccess(onDeployStore -> async.complete()));
            }));
        }));
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        super.stop(context);
    }

    @Test
    public void publishMessage(TestContext context) throws Exception {
        super.publishMessage(context, getJson());
    }

    @Override
    protected void checkResponse(JsonObject responseJson) {
        assertThat(responseJson.getBoolean(REGISTER_RESULT), instanceOf(Boolean.class));
    }

    @Override
    protected JsonObject getJson() {
        Vehicle vehicle = new VehicleFactoryFiat().getVehicle();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, JsonObject.mapFrom(vehicle).toString());
        return jsonObject;
    }

}