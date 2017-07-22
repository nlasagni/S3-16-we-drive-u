package com.wedriveu.services.vehicle.entity; /**
 * Created by Michele on 12/07/2017.
 */
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.utilities.Position;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class VehicleStoreImpl implements VehicleStore {

    @Override
    public void mapEntityToJson() {
        Vehicle vehicle = createDummyObject("MACCHINA1",
                        "broken",
                        new Position(10.2,13.2),
                        new Date(2017, 11, 30, 12, 37,43));
        Vehicle vehicle2 = createDummyObject("MACCHINA2",
                        "available",
                        new Position(11.2,14.2),
                        new Date(2017, 10, 28, 11, 43, 12));
        Vehicle vehicle3 = createDummyObject("MACCHINA3",
                        "busy",
                        new Position(15.2,13.2),
                        new Date(2017, 9, 26, 10, 56, 46));
        Vehicle vehicle4 = createDummyObject("MACCHINA4",
                        "recharging",
                        new Position(13.2,16.2),
                        new Date(2017, 8, 24, 9, 37, 22));

        ArrayList<Vehicle> vehicleListToJSon = new ArrayList<Vehicle>();
        vehicleListToJSon.add(vehicle);
        vehicleListToJSon.add(vehicle2);
        vehicleListToJSon.add(vehicle3);
        vehicleListToJSon.add(vehicle4);

        writeJsonVehicleFile(vehicleListToJSon);
    }

    @Override
    public Vehicle getVehicle(String carLicencePlate) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Vehicle[] vehicles = mapper.readValue(new File(Constants.VEHICLES_DATABASE_PATH), Vehicle[].class);
            return getVehicleRequestedCheckingVehiclesList(vehicles, carLicencePlate);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Vehicle createDummyObject(String carLicencePlate,
                                      String state,
                                      Position position,
                                      Date lastUpdate) {
        return new Vehicle(carLicencePlate, state,position, lastUpdate);
    }

    private void writeJsonVehicleFile(ArrayList<Vehicle> vehicleListToJSon) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new File(Constants.VEHICLES_DATABASE_PATH), vehicleListToJSon);
            String jsonInString = mapper.writeValueAsString(vehicleListToJSon);
            Log.log(jsonInString);
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vehicleListToJSon);
            Log.log(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Vehicle getVehicleRequestedCheckingVehiclesList(Vehicle[] vehicles, String carLicencePlate) {
        for (Vehicle vehicle : vehicles) {
            if(vehicle.getCarLicencePlate().equals(carLicencePlate)){
                Log.log("com.wedriveu.services.vehicle.entity.Vehicle found! -> " + vehicle.getCarLicencePlate() + " " + vehicle.getState());
                return vehicle;
            }
        }
        Log.log("com.wedriveu.services.vehicle.entity.Vehicle not found, retry!");
        return null;
    }

}
