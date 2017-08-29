package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.util.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryMini;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.RegisterToServiceResponse;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE;

@RunWith(VertxUnitRunner.class)
public class RegisterVehicleTestMini extends BaseInteractionClient {

    private static final String EVENT_BUS_ADDRESS = RegisterVehicleTestMini.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue.mini";
    private Async async;
    private Vertx vertx;
    private BootVerticle bootVerticle;

    public RegisterVehicleTestMini() {
        super(QUEUE, VEHICLE, REGISTER_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        async = context.async();
        vertx = Vertx.vertx();
        bootVerticle = new BootVerticle();
        super.setup(vertx, completed -> {
            vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, onCompleted -> {
                vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                    String licencePlate = new VehicleFactoryMini().getVehicle().getLicensePlate();
                    super.declareQueueAndBind(licencePlate, context, context.asyncAssertSuccess(declared -> {
                        async.complete();
                    }));
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
        super.publishMessageAndWaitResponse(context, VEHICLE, REGISTER_REQUEST, getJson());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        RegisterToServiceResponse response = responseJson.mapTo(RegisterToServiceResponse.class);
        context.assertTrue(response.getRegisterOk());
    }

    private JsonObject getJson() {
        Vehicle vehicle = new VehicleFactoryMini().getVehicle();
        return VertxJsonMapper.mapInBodyFrom(vehicle);
    }

}