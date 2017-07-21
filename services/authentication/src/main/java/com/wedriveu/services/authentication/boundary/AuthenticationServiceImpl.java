package com.wedriveu.services.authentication.boundary;

import com.wedriveu.services.authentication.control.CredentialsChecker;
import com.wedriveu.services.authentication.control.CredentialsCheckerImpl;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import com.wedriveu.services.shared.utilities.Util;


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
public class AuthenticationServiceImpl implements AuthenticationService {

	private CredentialsChecker checker = new CredentialsCheckerImpl();
	private HttpServer server;
	private Util utils = new Util();

	@Override
	public void startService() {
		Vertx vertx = Vertx.vertx();
		Router router = Router.router(vertx);
		router.get(utils.USER_LOGIN).handler(this::checkCredentials);
		server = vertx.createHttpServer();
		server.requestHandler(router::accept).listen(utils.PORT_AUTHENTICATION_SERVICE);
	}

	@Override
	public void stopService() {
		if(server != null){
			server.close();
		}
	}

	@Override
	public void checkCredentials(RoutingContext routingContext) {
		String username = routingContext.request().getParam(utils.USERNAME);
		String password = routingContext.request().getParam(utils.PASSWORD);
		HttpServerResponse response = routingContext.response();
		if (username == null || password == null) {
			response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(utils.USERNAME_PASSWORD_MISSING);
		} else {
			if (checker.confirmCredentials(username, password)) {
				response.putHeader(utils.CONTENT_TYPE, utils.TEXT_PLAIN)
						.setStatusCode(HttpResponseStatus.OK.code())
						.end(utils.USER_AUTHENTICATED);
			} else {
				response.setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end(utils.USERNAME_PASSWORD_WRONG);
			}
		}
	}

}
