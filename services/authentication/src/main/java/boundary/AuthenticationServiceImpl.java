package boundary;

import control.CredentialsChecker;
import control.CredentialsCheckerImpl;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


/**
 * Created by ste on 11/07/2017.
 * This class initialize a vertx server for the rest api.
 * It Starts a server that binds on port 8080 and waits for a get on http://ipaddress:8080/user/login
 * it has 2 parameters:
 * 	username, that is a string
 * 	password, that is a string
 * Can have 3 responses:
 *  400: username or password missing
 *  404: wrong username or password
 *  200: user authenticated
 */
public class AuthenticationServiceImpl {

	private CredentialsChecker checker = new CredentialsCheckerImpl();

	public void startServer() {
		Vertx vertx = Vertx.vertx();
		Router router = Router.router(vertx);
		router.get("/user/login").handler(this::checkCredentials);
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);

	}

	private void checkCredentials(RoutingContext routingContext) {
		String username = routingContext.request().getParam("username");
		String password = routingContext.request().getParam("password");
		HttpServerResponse response = routingContext.response();
		if (username == null || password == null) {
			response.setStatusCode(400).end("username or password missing");
		} else {
			if (checker.confirmCredentials(username, password)) {
				response.putHeader("content-type", "text/plain").end("user authenticated");
			} else {
				response.setStatusCode(404).end("wrong username or password");
			}
		}
	}

}
