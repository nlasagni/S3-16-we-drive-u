package com.wedriveu.services.vehicle.boundary.updates;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.vehicle.app.BootVerticle;
import com.wedriveu.services.vehicle.boundary.util.BaseInteractionClient;
import com.wedriveu.services.vehicle.boundary.util.mock.ChangeBookingMockVerticle;
import com.wedriveu.services.vehicle.boundary.util.mock.FindBookingPositionsMockVerticle;
import com.wedriveu.services.vehicle.boundary.util.mock.VehicleBookingMockVerticle;
import com.wedriveu.services.vehicle.boundary.util.mock.VehicleCanDriveMockVerticle;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryFiat;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryMini;
import com.wedriveu.services.vehicle.entity.VehicleStore;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicola Lasagni on 26/08/2017.
 */
@RunWith(VertxUnitRunner.class)
public class UpdatesVerticleTest extends BaseInteractionClient {

    private static final String QUEUE = UpdatesVerticleTest.class.getCanonicalName() + ".Queue";
    private static final String EVENT_BUS_ADDRESS =
            UpdatesVerticleTest.class.getCanonicalName() + ".EventBus";
    private static final String USERNAME = "dummyUsername";
    //Forli city, near the head quarter
    private static final Position SUBSTITUTION_VEHICLE_POSITION =
            new Position(44.199940, 12.094194);

    private Vertx vertx;
    private VehicleStore vehicleStore;
    private Vehicle brokenVehicle;
    private Vehicle substitutionVehicle;
    private BootVerticle bootVerticle;

    public UpdatesVerticleTest() {
        super(
                QUEUE,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_SUBSTITUTION, USERNAME),
                EVENT_BUS_ADDRESS
        );
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        Async async = context.async();
        vertx = Vertx.vertx();
        vehicleStore = new VehicleStoreImpl();
        bootVerticle = new BootVerticle();
        brokenVehicle = new VehicleFactoryMini().getVehicle();
        brokenVehicle.setStatus(Constants.Vehicle.STATUS_BOOKED);
        substitutionVehicle = new VehicleFactoryFiat().getVehicle();
        substitutionVehicle.setPosition(SUBSTITUTION_VEHICLE_POSITION);
        super.setup(vertx, completed -> {
            vertx.eventBus().consumer(Messages.VehicleService.BOOT_COMPLETED, onCompleted -> {
                vertx.eventBus().consumer(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, msg -> {
                    registerVehicle(brokenVehicle);
                    registerVehicle(substitutionVehicle);
                    deployMocks(handler -> {
                        super.declareQueueAndBind(null, context, declared -> {
                            context.assertTrue(declared.succeeded());
                            async.complete();
                        });
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

    private void deployMocks(Handler<AsyncResult<CompositeFuture>> handler) {
        List<Future> futures = new ArrayList<>();
        AbstractVerticle[] mockVerticles = {
                new FindBookingPositionsMockVerticle(USERNAME, brokenVehicle.getLicensePlate()),
                new VehicleBookingMockVerticle(USERNAME, substitutionVehicle.getLicensePlate()),
                new VehicleCanDriveMockVerticle(USERNAME, substitutionVehicle.getLicensePlate()),
                new ChangeBookingMockVerticle(USERNAME, substitutionVehicle.getLicensePlate())
        };
        for (AbstractVerticle verticle : mockVerticles) {
            Future<String> future = Future.future();
            vertx.deployVerticle(verticle, future.completer());
            futures.add(future);
        }
        CompositeFuture.all(futures).setHandler(handler);
    }

    private void registerVehicle(Vehicle vehicle) {
        vehicleStore.addVehicle(vehicle);
    }

    @Test
    public void substitution(TestContext context) {
        brokenVehicle.setStatus(Constants.Vehicle.STATUS_BROKEN_STOLEN);
        super.publishMessageAndWaitResponse(context,
                Constants.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE,
                createUpdate());
    }

    @Override
    protected void checkResponse(TestContext context, JsonObject responseJson) {
        VehicleResponse response = responseJson.mapTo(VehicleResponse.class);
        context.assertTrue(response.getNotEligibleVehicleFound() != null ||
                (response.getLicensePlate().equals(substitutionVehicle.getLicensePlate()) &&
                        response.getArriveAtUserTime() > 0 &&
                        response.getArriveAtDestinationTime() > 0));
    }

    private JsonObject createUpdate() {
        VehicleUpdate vehicleUpdate = new VehicleUpdate();
        vehicleUpdate.setUsername(USERNAME);
        vehicleUpdate.setLicense(brokenVehicle.getLicensePlate());
        vehicleUpdate.setStatus(brokenVehicle.getStatus());
        vehicleUpdate.setPosition(brokenVehicle.getPosition());
        vehicleUpdate.setUserOnBoard(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(vehicleUpdate).toString());
        return jsonObject;
    }

}