package com.wedriveu.services.authentication.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.authentication.control.CredentialsChecker;
import com.wedriveu.services.authentication.control.CredentialsCheckerImpl;
import com.wedriveu.services.authentication.util.Constants;
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.services.shared.util.Log;
import com.wedriveu.shared.entity.LoginRequest;
import com.wedriveu.shared.entity.LoginResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;

/**
 * @author Stefano Bernagozzi on 11/07/2017.
 * @author Nicola Lasagni on 04/08/2017
 *
 * This class represents the Boundary of the Authentication micro-service.
 * It allows users to login into the WeDriveU system through its API.
 */
public class AuthenticationServiceVerticleImpl extends AbstractVerticle implements AuthenticationService {

	private static final String TAG = AuthenticationServiceVerticleImpl.class.getSimpleName();
	private static final String QUEUE_NAME = "service.authentication";
	private static final String EVENT_BUS_ADDRESS = "service.authentication.login";
	private static final String READ_ERROR = "Error occurred while reading request.";
	private static final String SEND_ERROR = "Error occurred while sending response.";
	private static final String SERVICE_ILLEGAL_STATE = "The service has not been started yet or it has been stopped.";

	private CredentialsChecker checker;
	private ObjectMapper objectMapper;
	private RabbitMQClient rabbitMQClient;
	private EventBus eventBus;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		eventBus = vertx.eventBus();
		checker = new CredentialsCheckerImpl();
		objectMapper = new ObjectMapper();
		startService(startFuture);
	}

	private void startService(Future<Void> future) {
		rabbitMQClient = RabbitMQClientFactory.createClient(vertx);
		Future<Void> initFuture = Future.future();
		Future<Void> endFuture = Future.future();
		startClient(initFuture);
		initFuture.compose(v -> {
			Future<JsonObject> declareQueueFuture = Future.future();
			declareQueue(declareQueueFuture);
			return declareQueueFuture;
		}).compose(v -> {
			Future<Void> exchangeDeclareFuture = Future.future();
			exchangeDeclare(exchangeDeclareFuture);
			return exchangeDeclareFuture;
		}).compose(v -> {
			Future<Void> bindQueueFuture = Future.future();
			bindQueueToExchange(bindQueueFuture);
			return bindQueueFuture;
		}).compose(v -> {
			Future<Void> basicConsumeFuture = Future.future();
			basicConsume(basicConsumeFuture);
			return basicConsumeFuture;
		}).compose(v -> {
			registerConsumer();
			future.complete();
		}, endFuture);
	}

	private void startClient(Future<Void> future) {
		rabbitMQClient.start(future.completer());
	}

	private void declareQueue(Future<JsonObject> future) {
		rabbitMQClient.queueDeclare(QUEUE_NAME,
				true,
				false,
				false,
				future.completer());
	}

	private void exchangeDeclare(Future<Void> future) {
		rabbitMQClient.exchangeDelete(Constants.RabbitMQ.Exchanges.USER, onDelete -> {
			rabbitMQClient.exchangeDeclare(Constants.RabbitMQ.Exchanges.USER,
					Constants.RabbitMQ.Exchanges.Type.DIRECT,
					true,
					false,
					future.completer());
		});
	}

	private void bindQueueToExchange(Future<Void> future) {
		rabbitMQClient.queueBind(QUEUE_NAME,
				Constants.RabbitMQ.Exchanges.USER,
				Constants.RabbitMQ.RoutingKey.LOGIN,
				future.completer());
	}

	private void basicConsume(Future<Void> future) {
		rabbitMQClient.basicConsume(QUEUE_NAME, EVENT_BUS_ADDRESS, future.completer());
	}

	private void registerConsumer() {
		eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
			String requestId = "";
			LoginResponse response = new LoginResponse();
			try {
				JsonObject message = new JsonObject(msg.body().toString());
				LoginRequest loginRequest =
						objectMapper.readValue(message.getString(Constants.EventBus.BODY),
								LoginRequest.class);
				requestId = loginRequest.getRequestId();
				response = checkCredentials(loginRequest);
			} catch (IOException e) {
				Log.error(TAG, READ_ERROR, e);
				response.setErrorMessage(e.getMessage());
			}
			if (!requestId.trim().isEmpty()) {
				sendResponse(requestId, response);
			}
		});
	}

	private void sendResponse(String requestId, LoginResponse response) {
		try {
			String responseString = objectMapper.writeValueAsString(response);
			JsonObject responseJson = new JsonObject();
			responseJson.put(Constants.EventBus.BODY, responseString);
			rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.NO_EXCHANGE,
					requestId,
					responseJson,
					onPublish -> {
				if (!onPublish.succeeded()) {
					Log.error(TAG, SEND_ERROR, onPublish.cause());
				}
			});
		} catch (JsonProcessingException e) {
			Log.error(TAG, SEND_ERROR, e);
		}
	}

	@Override
	public LoginResponse checkCredentials(LoginRequest loginRequest) throws IllegalStateException {
		if (checkIllegalState()) {
			throw new IllegalStateException(SERVICE_ILLEGAL_STATE);
		}
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		LoginResponse response = new LoginResponse();
		if (username == null || password == null) {
			response.setErrorMessage(Constants.USERNAME_PASSWORD_MISSING);
		} else if (checker.confirmCredentials(username, password)) {
			response.setSuccess(true);
		} else {
			response.setErrorMessage(Constants.USERNAME_PASSWORD_WRONG);
		}
		return response;
	}

	private boolean checkIllegalState() {
		return context == null || deploymentID().isEmpty();
	}

}
