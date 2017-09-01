package com.wedriveu.services.shared.vertx;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;

/**
 * a class for mapping an object from and to a json object
 * @author Stefano Bernagozzi
 */
public class VertxJsonMapper {
    private static final String MAP_TO_ILLEGAL_ARGUMENT = "error, provided JsonObject to be mapped is null";
    private static final String MAP_FROM_ILLEGAL_ARGUMENT = "error, provided object to be mapped is null";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * maps from a body of a json object to a list of entities
     * @param jsonObject the json object with inside the entities
     * @param classType the entities that are inside the json object
     * @param <T> the objects of the returned list
     * @return a list of object T
     * @throws IOException if the mapper cannot read the list inside the json object
     */
    public static <T> List<T> mapFromBodyToList(JsonObject jsonObject, Class<T> classType) throws IOException {
        JavaType collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, classType);
        return MAPPER.readValue(jsonObject.getString(BODY), collectionType);
    }

    /**
     * maps from a json object to an entity contained in the json object
     * @param jsonObject the json object with inside the entity
     * @param classType the class of the entity that is inside the json object
     * @param <T> the type of the object returned
     * @return an object T
     * @throws IllegalArgumentException if the json object is null
     */
    public static <T> T mapTo(JsonObject jsonObject, Class<T> classType) throws IllegalArgumentException {
        if (jsonObject == null) {
            throw new IllegalArgumentException(MAP_TO_ILLEGAL_ARGUMENT);
        }
        return jsonObject.mapTo(classType);
    }
    /**
     * maps from a json object to an entity contained in the body of a json object
     * @param jsonObject the json object with inside the entity
     * @param classType the class of the entity that is inside the json object
     * @param <T> the type of the object returned
     * @return an object T
     * @throws IllegalArgumentException if the json object is null
     */
    public static <T> T mapFromBodyTo(JsonObject jsonObject, Class<T> classType) throws IllegalArgumentException {
        JsonObject body = new JsonObject(jsonObject.getString(BODY));
        return mapTo(body, classType);
    }

    /**
     * maps a list of objects T  into the body of a Json object
     * @param object the list that has to be mappped
     * @param <T> the classes of the object of the list
     * @return a json object with inside the body the list
     * @throws IOException if the mapper cannot write the list inside the json object
     */
    public static <T> JsonObject mapListInBodyFrom(T object) throws IOException {
        StringWriter sw =new StringWriter();
        MAPPER.writeValue(sw,object);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, sw.toString());
        return jsonObject;
    }

    /**
     * maps an object T  into a Json object
     * @param object the object that has to be mappped
     * @param <T> the class of the object
     * @return a json object with inside the object
     * @throws IllegalArgumentException if the object is null
     */
    public static <T> JsonObject mapFrom(T object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(MAP_FROM_ILLEGAL_ARGUMENT);
        }
        return JsonObject.mapFrom(object);
    }

    /**
     * maps an object T  into the body of a Json object
     * @param object the object that has to be mappped
     * @param <T> the class of the object
     * @return a json object with inside the body the object
     * @throws IllegalArgumentException if the object is null
     */
    public static <T> JsonObject mapInBodyFrom(T object) throws IllegalArgumentException {
        JsonObject body = mapFrom(object);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(BODY, body.toString());
        return jsonObject;
    }

}