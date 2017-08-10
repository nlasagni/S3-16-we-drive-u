
package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.boundary.PublisherTest;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactory;
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

    //inserire event_bus_address privati
    private static final String EVENT_BUS_ADDRESS = RegisterConsumerVerticle.class.getCanonicalName();
    private static final String QUEUE = "vehicle";
    private RegisterConsumerVerticle registerConsumerVerticle;

    public RegisterConsumerVerticleTest() {
        super(QUEUE, VEHICLE_SERVICE_EXCHANGE, ROUTING_KEY_REGISTER_VEHICLE_REQUEST,
                ROUTING_KEY_REGISTER_VEHICLE_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        registerConsumerVerticle = new RegisterConsumerVerticle();
        super.setup(context, registerConsumerVerticle);
       /* VehicleFactory factory = new VehicleFactoryA();
        Vehicle v = factory.getVehicle();
        String licencePlate = new VehicleFactoryA().getVehicle().getCarLicencePlate();*/
        super.declareQueueAndBind("AAA", context);
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
    protected JsonObject getJson() {
        Vehicle vehicle = new VehicleFactoryA().getVehicle();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, JsonObject.mapFrom(vehicle).toString());
        Log.info("REGISTER", jsonObject.encodePrettily());
        return jsonObject;
    }

}