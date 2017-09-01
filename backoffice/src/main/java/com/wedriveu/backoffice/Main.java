package com.wedriveu.backoffice;

import com.wedriveu.backoffice.controller.BackOfficeController;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Vertx;

/**
 * @author Stefano Bernagozzi
 */
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new BackOfficeController(vertx), completed -> {
            if (completed.succeeded()) {
                Log.info("correctly started backoffice");
            } else {
                Log.error("main backoffice", completed.cause().getLocalizedMessage(), completed.cause());
            }
        });
    }
}
