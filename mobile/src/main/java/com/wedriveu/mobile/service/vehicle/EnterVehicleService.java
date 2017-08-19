package com.wedriveu.mobile.service.vehicle;

import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public interface EnterVehicleService {

    void subscribeToEnterVehicle(ServiceOperationCallback<EnterVehicleRequest> callback);

}
