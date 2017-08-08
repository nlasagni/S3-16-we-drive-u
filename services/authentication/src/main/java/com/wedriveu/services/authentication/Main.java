package com.wedriveu.services.authentication;

import com.wedriveu.services.authentication.boundary.AuthenticationServiceVerticleImpl;
import io.vertx.core.Vertx;

/**
 * Created by Michele on 18/07/2017.
 */
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        AuthenticationServiceVerticleImpl authenticationServiceVerticle = new AuthenticationServiceVerticleImpl();
        vertx.deployVerticle(authenticationServiceVerticle);
    }

}
