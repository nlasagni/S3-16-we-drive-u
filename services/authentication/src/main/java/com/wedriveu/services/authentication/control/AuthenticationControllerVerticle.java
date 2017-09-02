package com.wedriveu.services.authentication.control;

import com.wedriveu.services.authentication.entity.UserStore;
import com.wedriveu.services.authentication.entity.UserStoreImpl;
import com.wedriveu.services.authentication.util.Constants;
import com.wedriveu.services.authentication.util.Credentials;
import com.wedriveu.services.shared.model.User;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.LoginRequest;
import com.wedriveu.shared.rabbitmq.message.LoginResponse;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.IOException;

/**
 * An {@link AbstractVerticle} that represents the effective implementation of the {@link AuthenticationController}.
 *
 * @author Nicola Lasagni on 02/09/2017.
 */
public class AuthenticationControllerVerticle extends AbstractVerticle implements AuthenticationController {

    private static final String STORE_ERROR = "Error while creating UserStore";

    private UserStore userStore;

    @Override
    public void start(Future startFuture) throws Exception {
        vertx.eventBus().consumer(Constants.EventBus.START_LOGIN, this::startLogin);
        vertx.executeBlocking(future -> {
            initStore();
            future.complete();
        }, result -> {
            startFuture.complete();
        });
    }

    private void initStore() {
        try {
            userStore = new UserStoreImpl();
        } catch (IOException e) {
            Log.error(this.getClass().getSimpleName(), STORE_ERROR, e);
        }
    }

    private void startLogin(Message<JsonObject> message) {
        LoginRequest request = VertxJsonMapper.mapFromBodyTo(message.body(), LoginRequest.class);
        String requestId = request.getRequestId();
        LoginResponse response = login(request);
        sendResponse(requestId, response);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        LoginResponse response = new LoginResponse();
        if (username == null || password == null) {
            response.setErrorMessage(Constants.USERNAME_PASSWORD_MISSING);
        } else if (Credentials.confirmCredentials(username, password)) {
            String userId = Credentials.generateUserId(username);
            boolean added = userStore.addUser(new User(userId, password));
            response.setSuccess(added);
            response.setUserId(userId);
        } else {
            response.setErrorMessage(Constants.USERNAME_PASSWORD_WRONG);
        }
        return response;
    }

    private void sendResponse(String requestId, LoginResponse response) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.Message.REQUEST_ID, requestId);
        jsonObject.put(Constants.Message.RESPONSE, VertxJsonMapper.mapFrom(response).toString());
        vertx.eventBus().send(Constants.EventBus.LOGIN_COMPLETED, jsonObject);
    }

}
