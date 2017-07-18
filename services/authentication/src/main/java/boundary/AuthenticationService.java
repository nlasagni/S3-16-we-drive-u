package boundary;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by Michele on 17/07/2017.
 */

/**
 * @Author Michele Donati
 */
public interface AuthenticationService {

    /**
     *  This method starts the service.
     */
    void startService();

    /**
     * This method stops the service.
     */
    void stopService();

    /**
     * This method checks the credentials from user's input.
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web.
     */
    void checkCredentials(RoutingContext routingContext);

}
