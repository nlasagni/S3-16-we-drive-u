package com.wedriveu.services.shared.utilities;

import com.wedriveu.services.shared.rabbitmq.nearest.VehicleResponse;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.shared.utilities.Constants.BODY;

/**
 * Created by Marco on 09/08/2017.
 */
public class MessageParser<T> {

    public static<T> T getObject(Message message, Class object) {
        JsonObject responseJson = (JsonObject) message.body();
        String response = responseJson.getString(BODY);
        return (new JsonObject(response)).mapTo((Class<T>) object);
    }

    public static JsonObject getJson(Message message) {
        JsonObject responseJson = (JsonObject) message.body();
        String response = responseJson.getString(BODY);
        return new JsonObject(response);
    }

}
