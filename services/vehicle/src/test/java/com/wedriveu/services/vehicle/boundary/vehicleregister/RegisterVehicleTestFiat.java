package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryFiat;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.RegisterToServiceResponse;
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
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE;
import static org.junit.Assert.assertTrue;


/**
 * Vehicle register simulator to the VehicleService. It simulates a new vehicle trying
 * to add itself in the VehicleService database.
 */
@RunWith(VertxUnitRunner.class)
public class RegisterVehicleTestFiat extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = RegisterVehicleTestFiat.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.fiat";

    public RegisterVehicleTestFiat() {
        super(QUEUE, VEHICLE, REGISTER_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        Async async = context.async();
        Vertx vertx = Vertx.vertx();
        super.setup(vertx, completed -> {
            vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, onCompleted -> {
                vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                    String licencePlate = new VehicleFactoryFiat().getVehicle().getLicensePlate();
                    super.declareQueueAndBind(licencePlate, context, declared -> {
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
    public void publishMessage(TestContext context) throws Exception {
        super.publishMessage(context, false, VEHICLE, REGISTER_REQUEST, getJson());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        RegisterToServiceResponse response = responseJson.mapTo(RegisterToServiceResponse.class);
        assertTrue(response.getRegisterOk());
    }

    private JsonObject getJson() {
        Vehicle vehicle = new VehicleFactoryFiat().getVehicle();
        return VertxJsonMapper.mapInBodyFrom(vehicle);
    }

}