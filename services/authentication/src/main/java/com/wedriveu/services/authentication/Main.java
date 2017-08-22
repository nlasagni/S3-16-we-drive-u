package com.wedriveu.services.authentication;

import com.wedriveu.services.authentication.boundary.AuthenticationBoundaryVerticleImpl;
import io.vertx.core.Vertx;

/**
 * Created by Michele on 18/07/2017.
 */
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        AuthenticationBoundaryVerticleImpl authenticationServiceVerticle = new AuthenticationBoundaryVerticleImpl();
        vertx.deployVerticle(authenticationServiceVerticle);
    }

}
