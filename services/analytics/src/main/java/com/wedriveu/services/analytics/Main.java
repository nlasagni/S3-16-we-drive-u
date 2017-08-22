package com.wedriveu.services.analytics;

import com.wedriveu.services.analytics.control.AnalyticsVerticleDeployer;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Vertx;

/**
 * @author Stefano Bernagozzi
 */


public class Main {

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AnalyticsVerticleDeployer(), completed -> {
            if (completed.succeeded()) {
                Log.info("correctly started Analytics service");
            } else {
                Log.error("main analytics service", completed.cause().getLocalizedMessage(), completed.cause());
            }
        });
    }

}
