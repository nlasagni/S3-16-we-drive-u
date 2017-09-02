package com.wedriveu.services.authentication.boundary;

import com.wedriveu.shared.rabbitmq.message.LoginRequest;
import com.wedriveu.shared.rabbitmq.message.LoginResponse;

/**
 * @author Michele Donati
 * @author Nicola Lasagni on 04/08/2017
 *         <p>
 *         This interface represents the Boundary of the Authentication micro-service.
 *         It allows users to login into the WeDriveU system through its API.
 */
public interface AuthenticationBoundary {

    /**
     * This method checks the credentials from user's input.
     *
     * @param loginRequest The request with which check the credentials.
     */
    void login(LoginRequest loginRequest);

}
