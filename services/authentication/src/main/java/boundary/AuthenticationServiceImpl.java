package boundary;

import control.CredentialsChecker;
import control.CredentialsCheckerImpl;
import io.netty.handler.codec.http.HttpResponseStatus;
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
public class AuthenticationServiceImpl implements AuthenticationService {

	//FIXME Fix the package dependencies for insert all of these constants in shared/Util.java and use it here
	public static final String USER_LOGIN = "/user/login";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String USERNAME_PASSWORD_MISSING = "username or password missing";
	public static final String USERNAME_PASSWORD_WRONG = "wrong username or password";
	public static final String USER_AUTHENTICATED = "user authenticated";
	public static final String CONTENT_TYPE = "content-type";
	public static final String TEXT_PLAIN = "text/plain";
	public static final int PORT_AUTHENTICATION_SERVICE = 8080;
	private CredentialsChecker checker = new CredentialsCheckerImpl();
	private HttpServer server;

	@Override
	public void startService() {
		Vertx vertx = Vertx.vertx();
		Router router = Router.router(vertx);
		router.get(USER_LOGIN).handler(this::checkCredentials);
		server = vertx.createHttpServer();
		server.requestHandler(router::accept).listen(PORT_AUTHENTICATION_SERVICE);
	}

	@Override
	public void stopService() {
		if(server != null){
			server.close();
		}
	}

	@Override
	public void checkCredentials(RoutingContext routingContext) {
		String username = routingContext.request().getParam(USERNAME);
		String password = routingContext.request().getParam(PASSWORD);
		HttpServerResponse response = routingContext.response();
		if (username == null || password == null) {
			response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(USERNAME_PASSWORD_MISSING);
		} else {
			if (checker.confirmCredentials(username, password)) {
				response.putHeader(CONTENT_TYPE, TEXT_PLAIN)
						.setStatusCode(HttpResponseStatus.OK.code())
						.end(USER_AUTHENTICATED);
			} else {
				response.setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end(USERNAME_PASSWORD_WRONG);
			}
		}
	}

	public static void main(String[] args) {
		new AuthenticationServiceImpl().startService();
	}

}
