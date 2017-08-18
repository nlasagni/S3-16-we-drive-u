package com.wedriveu.backoffice;

import com.wedriveu.backoffice.controller.BackofficeController;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Vertx;

import static java.lang.Thread.sleep;

/**
 * @author Stefano Bernagozzi
 */
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new BackofficeController(vertx), completed -> {
            if (completed.succeeded()) {
                Log.log("correctly started Analytics service");
            } else {
                Log.error("main analytics service", completed.cause().getLocalizedMessage(), completed.cause());
            }
        });

    }
}
