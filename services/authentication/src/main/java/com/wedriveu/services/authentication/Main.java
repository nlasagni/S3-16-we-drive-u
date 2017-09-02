package com.wedriveu.services.authentication;

import com.wedriveu.services.authentication.boundary.AuthenticationBoundaryVerticle;
import com.wedriveu.services.authentication.control.AuthenticationControllerVerticle;
import io.vertx.core.Vertx;

/**
 * @author Michele Donati on 18/07/2017.
 * @author Nicola Lasagni
 */
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AuthenticationControllerVerticle(), onControllerDeployed -> {
            vertx.deployVerticle(new AuthenticationBoundaryVerticle());
        });
    }

}
