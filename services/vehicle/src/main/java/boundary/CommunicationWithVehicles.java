package boundary;

import java.io.IOException;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 */
public interface CommunicationWithVehicles {
    public double requestBatteryPercentage(String licensePlate) throws IOException;

}
