package com.wedriveu.shared.rabbitmq.communication;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Utility class to manage message mapping for RabbitMQ.
 *
 * @author Nicola Lasagni on 08/08/2017.
 */
class RabbitMqMessageMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maps an object to a json string and then gets its byte[].
     *
     * @param <E>    the type parameter of the object to be mapped
     * @param object the object to be mapped
     * @return the byte[] obtained from the object mapping
     * @throws IOException if something goes wrong during the mapping
     */
    static <E> byte[] mapToByteArray(E object) throws IOException {
        return objectMapper.writeValueAsString(object).getBytes();
    }

    /**
     * Maps a byte[] to an object of type {@linkplain E}.
     *
     * @param <E>    the type parameter with which perform the mapping
     * @param bytes  the content to be mapped
     * @param eClass the {@linkplain Class<E>} used for the mapping
     * @return an instance of {@linkplain E}
     * @throws IOException if something goes wrong during the mapping
     */
    static <E> E mapFromByteArray(byte[] bytes, Class<E> eClass) throws IOException {
        String jsonString = new String(bytes);
        return objectMapper.readValue(jsonString, eClass);
    }

}
