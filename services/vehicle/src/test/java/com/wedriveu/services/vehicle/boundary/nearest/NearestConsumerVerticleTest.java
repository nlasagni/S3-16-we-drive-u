package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.vehicle.boundary.PublisherTest;
import com.wedriveu.services.vehicle.boundary.nearest.entity.UserDataFactoryA;
import com.wedriveu.services.vehicle.rabbitmq.UserRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.wedriveu.services.shared.utilities.Constants.*;


@RunWith(VertxUnitRunner.class)
public class NearestConsumerVerticleTest extends PublisherTest {

    private static final String EVENT_BUS_ADDRESS = NearestConsumerVerticleTest.class.getCanonicalName();

    private NearestConsumerVerticle registerConsumerVerticle;


    @Before
    public void setUp(TestContext context) throws Exception {
        registerConsumerVerticle = new NearestConsumerVerticle();
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
                ROUTING_KEY_VEHICLE_REQUEST,
                EVENT_BUS_ADDRESS,
                createRequestJsonObject());

    }

    @Override
    protected JsonObject createRequestJsonObject() {
        UserRequest userData = new UserDataFactoryA().getUserData();
        JsonObject jsonObject = new JsonObject().mapFrom(userData);
        jsonObject.put(BODY, jsonObject);
        return jsonObject;
    }

}
