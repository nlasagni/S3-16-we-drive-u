package com.wedriveu.services.shared.vertx;

import com.fasterxml.jackson.databind.JavaType;
import com.wedriveu.services.shared.utilities.Constants;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
public class VertxJsonMapper {
    private static final String MAP_TO_ILLEGAL_ARGUMENT = "error, provided JsonObject to be mapped is null";
    private static final String MAP_FROM_ILLEGAL_ARGUMENT = "error, provided object to be mapped is null";

    public static <T> T mapTo(JsonObject jsonObject, Class<T> classType)throws IllegalArgumentException {
        if (jsonObject == null) {
            throw new IllegalArgumentException(MAP_TO_ILLEGAL_ARGUMENT);
        }
        return jsonObject.mapTo(classType);
    }

    public static <T> T mapFromBodyTo(JsonObject jsonObject, Class<T> classType)throws IllegalArgumentException {
        JsonObject body = new JsonObject(jsonObject.getString(Constants.BODY));
        return mapTo(body, classType);
    }


    public static <T> JsonObject mapFrom(T object) throws IllegalArgumentException{
        if (object == null) {
            throw new IllegalArgumentException(MAP_FROM_ILLEGAL_ARGUMENT);
        }
        return JsonObject.mapFrom(object);
    }

    public static <T> JsonObject mapInBodyFrom(T object) throws IllegalArgumentException {
        JsonObject body = mapFrom(object);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.BODY, body.toString());
        return jsonObject;
    }
}
