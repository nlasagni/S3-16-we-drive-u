package com.wedriveu.services.analytics;

import com.wedriveu.services.analytics.control.*;
import io.vertx.core.Vertx;
import com.wedriveu.services.shared.utilities.Log;

/**
 * @Author Stefano Bernagozzi
 */



public class Main {

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AnalyticsVerticleDeployer(), completed -> {
            if (completed.succeeded()) {
                Log.log("correctly started Analytics service");
            } else {
                Log.error("main analytics service", completed.cause().getLocalizedMessage(), completed.cause());
            }
        });
    }

}
