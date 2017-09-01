package com.wedriveu.mobile.service.vehicle;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.store.VehicleStore;
import com.wedriveu.shared.rabbitmq.communication.DefaultRabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.config.RabbitMqCommunicationConfig;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.*;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class VehicleServiceImpl implements VehicleService {

    private static final String TAG = VehicleServiceImpl.class.getSimpleName();
    private static final String START_ERROR = "Error occurred while starting service.";
    private static final String STOP_ERROR = "Error occurred while stopping service.";
    private static final String ENTER_CONFIRMATION = "confirmed";
    private static final String ENTER_ERROR = "Error occurred while confirming enter operation.";
    private static final String REGISTER_ERROR = "Error occurred while registering consumer.";
    private static final String UNREGISTER_ERROR = "Error occurred while unregistering consumer.";

    private UserStore mUserStore;
    private VehicleStore mVehicleStore;
    private RabbitMqCommunicationManager communicationManager;

    private String enterConsumerTag;
    private String arrivedConsumerTag;
    private String updateConsumerTag;
    private String substitutionConsumerTag;

    public VehicleServiceImpl(UserStore userStore, VehicleStore vehicleStore) {
        mUserStore = userStore;
        mVehicleStore = vehicleStore;
        communicationManager = new DefaultRabbitMqCommunicationManager();
    }

    @Override
    public <T> void start(final ServiceOperationHandler<T, Void> handler) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String error = "";
                try {
                    ExceptionHandler exceptionHandler = new ServiceExceptionHandler();
                    RabbitMqCommunicationConfig config =
                            new RabbitMqCommunicationConfig.Builder()
                                    .host(Constants.RabbitMQ.Broker.HOST)
                                    .password(Constants.RabbitMQ.Broker.PASSWORD)
                                    .exceptionHandler(exceptionHandler).build();
                    communicationManager.setUpCommunication(config);
                } catch (IOException | TimeoutException e) {
                    Log.e(TAG, START_ERROR, e);
                    error = e.getLocalizedMessage();
                }
                Message message = handler.obtainMessage();
                message.obj = new ServiceResult<>(null, error);
                handler.sendMessage(message);
                return null;
            }
        }.execute();
    }

    @Override
    public <T> void stop(final ServiceOperationHandler<T, Void> handler) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String error = "";
                try {
                    communicationManager.closeCommunication();
                } catch (IOException | TimeoutException | AlreadyClosedException e) {
                    Log.e(TAG, STOP_ERROR, e);
                    error = e.getLocalizedMessage();
                }
                Message message = handler.obtainMessage();
                message.obj = new ServiceResult<>(null, error);
                handler.sendMessage(message);
                return null;
            }
        }.execute();
    }

    @Override
    public <T> void subscribeToEnterVehicle(final ServiceOperationHandler<T, EnterVehicleRequest> handler) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String username = mUserStore.getUser().getUsername();
                RabbitMqConsumerStrategy<EnterVehicleRequest> strategy =
                        new EnterVehicleConsumerStrategy(username, handler);
                enterConsumerTag = registerConsumer(strategy, EnterVehicleRequest.class);
                return null;
            }
        }.execute();
    }

    @Override
    public <T> void enterVehicleAndUnsubscribe(final ServiceOperationHandler<T, Void> handler) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String licensePlate = mVehicleStore.getVehicle().getLicencePlate();
                EnterVehicleResponse enterVehicleResponse = new EnterVehicleResponse();
                enterVehicleResponse.setResponse(ENTER_CONFIRMATION);
                String error = "";
                try {
                    communicationManager.publishMessage(Constants.RabbitMQ.Exchanges.VEHICLE,
                            String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE_ENTER_USER, licensePlate),
                            enterVehicleResponse);
                    communicationManager.unregisterConsumer(enterConsumerTag);
                } catch (IOException e) {
                    Log.e(TAG, ENTER_ERROR, e);
                    error = e.getLocalizedMessage();
                }
                Message message = handler.obtainMessage();
                message.obj = new ServiceResult<>(null, error);
                handler.sendMessage(message);
                return null;
            }
        }.execute();
    }

    @Override
    public void unsubscribeToEnterVehicle() {
        unregisterConsumer(enterConsumerTag);
    }

    @Override
    public <T> void subscribeToVehicleArrived(final ServiceOperationHandler<T, CompleteBookingResponse> handler) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String username = mUserStore.getUser().getUsername();
                RabbitMqConsumerStrategy<CompleteBookingResponse> strategy =
                        new VehicleArrivedConsumerStrategy(username, handler);
                arrivedConsumerTag = registerConsumer(strategy, CompleteBookingResponse.class);
                return null;
            }
        }.execute();
    }
    @Override
    public void unsubscribeToVehicleArrived() {
        unregisterConsumer(arrivedConsumerTag);
    }

    @Override
    public <T> void subscribeToVehicleUpdates(final ServiceOperationHandler<T, VehicleUpdate> handler) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                RabbitMqConsumerStrategy<VehicleUpdate> strategy =
                        new VehicleUpdateConsumerStrategy(handler);
                updateConsumerTag = registerConsumer(strategy, VehicleUpdate.class);
                return null;
            }
        }.execute();
    }

    @Override
    public void unsubscribeToVehicleUpdates() {
        unregisterConsumer(updateConsumerTag);
    }

    @Override
    public <T> void subscribeToVehicleSubstitution(final ServiceOperationHandler<T, Vehicle> handler) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String username = mUserStore.getUser().getUsername();
                RabbitMqConsumerStrategy<VehicleResponse> strategy =
                        new VehicleSubstitutionConsumerStrategy(username, handler);
                substitutionConsumerTag = registerConsumer(strategy, VehicleResponse.class);
                return null;
            }
        }.execute();
    }

    @Override
    public void unsubscribeToVehicleSubstitution() {
        unregisterConsumer(substitutionConsumerTag);
    }

    private <T> String registerConsumer(RabbitMqConsumerStrategy<T> strategy,
                                               Class<T> eClass) {
        try {
            return communicationManager.registerConsumer(strategy, eClass);
        } catch (IOException e) {
            Log.e(TAG, REGISTER_ERROR, e);
        }
        return null;
    }

    private void unregisterConsumer(final String consumerTag) {
        new AsyncTask<Void, Void, ServiceResult<Boolean>>() {
            @Override
            protected ServiceResult<Boolean> doInBackground(Void... voids) {
                try {
                    communicationManager.unregisterConsumer(consumerTag);
                } catch (IOException | AlreadyClosedException e) {
                    Log.e(TAG, UNREGISTER_ERROR, e);
                }
                return null;
            }
        }.execute();
    }

}
