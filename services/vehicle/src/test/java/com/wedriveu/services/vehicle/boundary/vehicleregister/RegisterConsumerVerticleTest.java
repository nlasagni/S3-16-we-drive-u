
package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.vehicle.boundary.PublisherTest;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryA;
import com.wedriveu.services.vehicle.entity.Vehicle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.wedriveu.services.shared.utilities.Constants.*;


@RunWith(VertxUnitRunner.class)
public class RegisterConsumerVerticleTest extends PublisherTest {

    private static final String EVENT_BUS_ADDRESS = RegisterConsumerVerticle.class.getCanonicalName();

    private RegisterConsumerVerticle registerConsumerVerticle;


    @Before
    public void setUp(TestContext context) throws Exception {
        registerConsumerVerticle = new RegisterConsumerVerticle();
        super.setup(context, registerConsumerVerticle);
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        super.stop(context);
    }

    @Test
    public void registerConsumer(TestContext context) throws Exception {
        super.registerConsumer(context,
                VEHICLE_SERVICE_EXCHANGE,
                ROUTING_KEY_REGISTER_VEHICLE_REQUEST,
                EVENT_BUS_ADDRESS,
                createRequestJsonObject());

    }

    @Override
    protected JsonObject createRequestJsonObject() {
        Vehicle vehicle = new VehicleFactoryA().getVehicle();
        JsonObject jsonObject = new JsonObject().mapFrom(vehicle);
        jsonObject.put(BODY, jsonObject);
        return jsonObject;
    }

}