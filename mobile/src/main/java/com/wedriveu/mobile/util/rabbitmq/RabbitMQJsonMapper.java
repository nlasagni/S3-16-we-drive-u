package com.wedriveu.mobile.util.rabbitmq;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author Nicola Lasagni on 08/08/2017.
 */
public class RabbitMQJsonMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <E> String mapToJsonString(E object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    public static <E> byte[] mapToByteArray(E object) throws IOException {
        return mapToJsonString(object).getBytes();
    }

    public static <E> E mapFromJsonString(String jsonString, Class<E> eClass) throws IOException {
        return objectMapper.readValue(jsonString, eClass);
    }

    public static <E> E mapFromByteArray(byte[] bytes, Class<E> eClass) throws IOException {
        String jsonString = new String(bytes);
        return mapFromJsonString(jsonString, eClass);
    }

}
