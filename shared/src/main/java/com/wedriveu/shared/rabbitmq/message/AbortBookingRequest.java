package com.wedriveu.shared.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nicola Lasagni on 30/08/2017.
 */
public class AbortBookingRequest {

    private String username;

    public AbortBookingRequest(@JsonProperty("username") String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
