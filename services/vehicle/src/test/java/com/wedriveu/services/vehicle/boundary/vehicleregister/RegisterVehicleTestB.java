package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.vehicle.boundary.PublisherTest;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryA;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryB;
import com.wedriveu.services.vehicle.entity.Vehicle;
import io.vertx.core.json.JsonObject;
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
public class RegisterVehicleTestB extends PublisherTest {

    private static final String EVENT_BUS_ADDRESS = RegisterConsumerVerticle.class.getCanonicalName();
    private static final String QUEUE = "vehicle.queue";
    private RegisterConsumerVerticle registerConsumerVerticle;

    public RegisterVehicleTestB() {
        super(QUEUE, VEHICLE_SERVICE_EXCHANGE, ROUTING_KEY_REGISTER_VEHICLE_REQUEST,
                ROUTING_KEY_REGISTER_VEHICLE_RESPONSE, EVENT_BUS_ADDRESS);
    }

    @Before
    public void setUp(TestContext context) throws Exception {
        registerConsumerVerticle = new RegisterConsumerVerticle();
        super.setup(context, registerConsumerVerticle);
        String licencePlate = new VehicleFactoryB().getVehicle().getCarLicencePlate();
        super.declareQueueAndBind(licencePlate, context);
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
        Vehicle vehicle = new VehicleFactoryB().getVehicle();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, JsonObject.mapFrom(vehicle).toString());
        return jsonObject;
    }

}