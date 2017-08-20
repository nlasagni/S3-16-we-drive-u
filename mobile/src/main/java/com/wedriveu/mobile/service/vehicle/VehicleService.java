package com.wedriveu.mobile.service.vehicle;

import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingResponse;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public interface VehicleService {

    void subscribeToEnterVehicle(ServiceOperationCallback<EnterVehicleRequest> callback);

    void enterVehicleAndUnsubscribe(ServiceOperationCallback<Void> callback);

    void subscribeToVehicleArrived(ServiceOperationCallback<CompleteBookingResponse> callback);

    void unsubscribeToVehicleArrived();

    void subscribeToVehiclePositionChanged(ServiceOperationCallback<Vehicle> callback);

    void unsubscribeToVehiclePositionChanged();

}
