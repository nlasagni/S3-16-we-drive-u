package com.wedriveu.services.authentication.boundary;

import com.wedriveu.shared.rabbitmq.message.LoginRequest;
import com.wedriveu.shared.rabbitmq.message.LoginResponse;

/**
 * @author Michele Donati
 * @author Nicola Lasagni on 04/08/2017
 *
 * This interface represents the Boundary of the Authentication micro-service.
 * It allows users to login into the WeDriveU system through its API.
 */
public interface AuthenticationService {

    /**
     * This method checks the credentials from user's input.
     * @param loginRequest The request with which check the credentials.
     * @return A {@linkplain LoginResponse} as a result of this check operation.
     * @throws IllegalStateException If the service has not been started yet or if it has been stopped.
     */
    LoginResponse checkCredentials(LoginRequest loginRequest) throws IllegalStateException;

}
