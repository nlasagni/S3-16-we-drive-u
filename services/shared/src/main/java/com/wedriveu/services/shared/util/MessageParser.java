package com.wedriveu.services.shared.util;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;


/**
 * Created by Marco on 09/08/2017.
 */
public class MessageParser {

    public static JsonObject getJson(Message message) {
        JsonObject responseJson = (JsonObject) message.body();
        String response = responseJson.getString(BODY);
        return new JsonObject(response);
    }

}
