package com.wedriveu.mobile.service.vehicle;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.store.VehicleStore;
import com.wedriveu.shared.rabbitmq.communication.DefaultRabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.config.RabbitMqCommunicationConfig;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqCloseCommunicationStrategy;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingResponse;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleResponse;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class VehicleServiceImpl implements VehicleService {

    private static final String TAG = VehicleServiceImpl.class.getSimpleName();
    private static final String ENTER_CONFIRMATION = "confirmed";
    private static final String ENTER_ERROR = "Error occurred while confirming enter operation.";
    private static final String ARRIVED_ERROR = "Error occurred while managing vehicle arrived operation.";
    private static final String UPDATE_ERROR = "Error occurred while managing vehicle update operation.";

    private Activity mActivity;
    private UserStore mUserStore;
    private VehicleStore mVehicleStore;
    private RabbitMqCommunicationManager mEnterVehicleCommunicationManager;
    private RabbitMqCommunicationManager mVehicleArrivedCommunicationManager;
    private RabbitMqCommunicationManager mVehiclePositionCommunicationManager;

    public VehicleServiceImpl(Activity activity, UserStore userStore, VehicleStore vehicleStore) {
        mActivity = activity;
        mUserStore = userStore;
        mVehicleStore = vehicleStore;
        mEnterVehicleCommunicationManager = new DefaultRabbitMqCommunicationManager();
        mVehicleArrivedCommunicationManager = new DefaultRabbitMqCommunicationManager();
        mVehiclePositionCommunicationManager = new DefaultRabbitMqCommunicationManager();
    }

    @Override
    public void subscribeToEnterVehicle(final ServiceOperationCallback<EnterVehicleRequest> callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ExceptionHandler exceptionHandler = new ServiceExceptionHandler();
                    RabbitMqCommunicationConfig config =
                            new RabbitMqCommunicationConfig.Builder()
                                    .host(Constants.RabbitMQ.Broker.HOST)
                                    .password(Constants.RabbitMQ.Broker.PASSWORD)
                                    .exceptionHandler(exceptionHandler).build();
                    mEnterVehicleCommunicationManager.setUpCommunication(config);
                    RabbitMqConsumerStrategy<EnterVehicleRequest> strategy =
                            new EnterVehicleConsumerStrategy(mActivity, mUserStore.getUser(), callback);
                    mEnterVehicleCommunicationManager.registerConsumer(strategy, EnterVehicleRequest.class);
                } catch (IOException | TimeoutException e) {
                    Log.e(TAG, ENTER_ERROR, e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void enterVehicleAndUnsubscribe(ServiceOperationCallback<Void> callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                EnterVehicleResponse enterVehicleResponse = new EnterVehicleResponse();
                enterVehicleResponse.setResponse(ENTER_CONFIRMATION);
                try {
                    mEnterVehicleCommunicationManager.publishMessage(Constants.RabbitMQ.Exchanges.VEHICLE,
                            Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE_ENTER_USER,
                            enterVehicleResponse);
                    mEnterVehicleCommunicationManager.closeCommunication(new RabbitMqCloseCommunicationStrategy() {
                        @Override
                        public void closeCommunication(RabbitMqCommunication communication) throws IOException {
                            communication.getChannel().queueDelete(
                                    String.format(com.wedriveu.mobile.util.Constants.Queue.ENTER_VEHICLE,
                                            mUserStore.getUser().getUsername()));
                        }
                    });
                } catch (IOException | TimeoutException e) {
                    Log.e(TAG, ENTER_ERROR, e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void subscribeToVehicleArrived(final ServiceOperationCallback<CompleteBookingResponse> callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ExceptionHandler exceptionHandler = new ServiceExceptionHandler();
                    RabbitMqCommunicationConfig config =
                            new RabbitMqCommunicationConfig.Builder()
                                    .host(Constants.RabbitMQ.Broker.HOST)
                                    .password(Constants.RabbitMQ.Broker.PASSWORD)
                                    .exceptionHandler(exceptionHandler).build();
                    mVehicleArrivedCommunicationManager.setUpCommunication(config);
                    RabbitMqConsumerStrategy<CompleteBookingResponse> strategy =
                            new VehicleArrivedConsumerStrategy(mActivity, mUserStore.getUser(), callback);
                    mVehicleArrivedCommunicationManager.registerConsumer(strategy, CompleteBookingResponse.class);
                } catch (IOException | TimeoutException e) {
                    Log.e(TAG, ARRIVED_ERROR, e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void unsubscribeToVehicleArrived() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mVehicleArrivedCommunicationManager.closeCommunication(new RabbitMqCloseCommunicationStrategy() {
                        @Override
                        public void closeCommunication(RabbitMqCommunication communication) throws IOException {
                            communication.getChannel().queueDelete(
                                    String.format(com.wedriveu.mobile.util.Constants.Queue.VEHICLE_ARRIVED,
                                            mUserStore.getUser().getUsername()));
                        }
                    });
                } catch (IOException | TimeoutException e) {
                    Log.e(TAG, ARRIVED_ERROR, e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void subscribeToVehiclePositionChanged(final ServiceOperationCallback<Vehicle> callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ExceptionHandler exceptionHandler = new ServiceExceptionHandler();
                    RabbitMqCommunicationConfig config =
                            new RabbitMqCommunicationConfig.Builder()
                                    .host(Constants.RabbitMQ.Broker.HOST)
                                    .password(Constants.RabbitMQ.Broker.PASSWORD)
                                    .exceptionHandler(exceptionHandler).build();
                    mVehiclePositionCommunicationManager.setUpCommunication(config);
                    RabbitMqConsumerStrategy<UpdateToService> strategy =
                            new VehicleUpdateConsumerStrategy(mActivity, mUserStore.getUser(), mVehicleStore, callback);
                    mVehiclePositionCommunicationManager.registerConsumer(strategy, UpdateToService.class);
                } catch (IOException | TimeoutException e) {
                    Log.e(TAG, ARRIVED_ERROR, e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void unsubscribeToVehiclePositionChanged() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mVehiclePositionCommunicationManager.closeCommunication(new RabbitMqCloseCommunicationStrategy() {
                        @Override
                        public void closeCommunication(RabbitMqCommunication communication) throws IOException {
                            communication.getChannel().queueDelete(
                                    String.format(com.wedriveu.mobile.util.Constants.Queue.VEHICLE_UPDATE,
                                            mUserStore.getUser().getUsername()));
                        }
                    });
                } catch (IOException | TimeoutException e) {
                    Log.e(TAG, UPDATE_ERROR, e);
                }
                return null;
            }
        }.execute();
    }

}
