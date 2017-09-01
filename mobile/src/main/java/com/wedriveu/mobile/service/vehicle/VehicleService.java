package com.wedriveu.mobile.service.vehicle;

import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingResponse;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;

/**
 * Service that manages all vehicle remote communications, so it exposes the capability of subscribing
 * and unsubscribing to all the main vehicle events like:
 * updates, entering, arrived at destination and substitution.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public interface VehicleService {

    /**
     * Starts the service.
     *
     * @param <T>     the type of the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     * @param handler the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     */
    <T> void start(ServiceOperationHandler<T, Void> handler);

    /**
     * Stops the service.
     *
     * @param <T>     the type of the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     * @param handler the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     */
    <T> void stop(ServiceOperationHandler<T, Void> handler);

    /**
     * Subscribe the {@code handler} provided to enter vehicle request events.
     *
     * @param <T>     the type of the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     * @param handler the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     */
    <T> void subscribeToEnterVehicle(ServiceOperationHandler<T, EnterVehicleRequest> handler);

    /**
     * Enters into a vehicle and unsubscribe from that events.
     *
     * @param <T>     the type of the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     * @param handler the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     */
    <T> void enterVehicleAndUnsubscribe(ServiceOperationHandler<T, Void> handler);

    /**
     * Unsubscribe to enter vehicle request events.
     */
    void unsubscribeToEnterVehicle();

    /**
     * Subscribes the {@code handler} provided to vehicle arrived events.
     *
     * @param <T>     the type of the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     * @param handler the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     */
    <T> void subscribeToVehicleArrived(ServiceOperationHandler<T, CompleteBookingResponse> handler);

    /**
     * Unsubscribe to vehicle arrived events.
     */
    void unsubscribeToVehicleArrived();

    /**
     * Subscribe the {@code handler} provided to vehicle updates.
     *
     * @param <T>     the type of the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     * @param handler the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     */
    <T> void subscribeToVehicleUpdates(ServiceOperationHandler<T, VehicleUpdate> handler);

    /**
     * Unsubscribe to vehicle update events.
     */
    void unsubscribeToVehicleUpdates();

    /**
     * Subscribe the {@code handler} provided to vehicle substitution events.
     *
     * @param <T>     the type of the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     * @param handler the handler of the {@link com.wedriveu.mobile.service.ServiceResult}
     */
    <T> void subscribeToVehicleSubstitution(ServiceOperationHandler<T, Vehicle> handler);

    /**
     * Unsubscribe to vehicle substitution events.
     */
    void unsubscribeToVehicleSubstitution();

}
