package com.wedriveu.services.shared.vertx;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;

/**
 * @author Stefano Bernagozzi
 */
public class VertxJsonMapper {
    private static final String MAP_TO_ILLEGAL_ARGUMENT = "error, provided JsonObject to be mapped is null";
    private static final String MAP_FROM_ILLEGAL_ARGUMENT = "error, provided object to be mapped is null";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> List<T> mapFromBodyToList(JsonObject jsonObject, Class<T> classType) throws IOException {
        JavaType collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, classType);
        return MAPPER.readValue(jsonObject.getString(BODY), collectionType);
    }

    public static <T> T mapTo(JsonObject jsonObject, Class<T> classType) throws IllegalArgumentException {
        if (jsonObject == null) {
            throw new IllegalArgumentException(MAP_TO_ILLEGAL_ARGUMENT);
        }
        return jsonObject.mapTo(classType);
    }

    public static <T> T mapFromBodyTo(JsonObject jsonObject, Class<T> classType) throws IllegalArgumentException {
        JsonObject body = new JsonObject(jsonObject.getString(BODY));
        return mapTo(body, classType);
    }

    public static <T> JsonObject mapListInBodyFrom(T object) throws IOException {
        StringWriter sw =new StringWriter();
        MAPPER.writeValue(sw,object);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, sw.toString());
        return jsonObject;
    }

    public static <T> JsonObject mapFrom(T object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(MAP_FROM_ILLEGAL_ARGUMENT);
        }
        return JsonObject.mapFrom(object);
    }

    public static <T> JsonObject mapInBodyFrom(T object) throws IllegalArgumentException {
        JsonObject body = mapFrom(object);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, body.toString());
        return jsonObject;
    }

}
