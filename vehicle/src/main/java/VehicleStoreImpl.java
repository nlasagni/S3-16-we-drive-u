/**
 * Created by Michele on 12/07/2017.
 */
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class VehicleStoreImpl implements VehicleStore {

    @Override
    public void mapVehiclesToJSon() {
        ObjectMapper mapper = new ObjectMapper();

        Vehicle vehicle = createDummyObject("MACCHINA1", "broken", 10.2, 13.2, new Date(2017, 11, 30, 12, 37,43));
        Vehicle vehicle2 = createDummyObject("MACCHINA2", "available", 11.2, 14.2, new Date(2017, 10, 28, 11, 43, 12));
        Vehicle vehicle3 = createDummyObject("MACCHINA3", "busy", 12.2, 15.2, new Date(2017, 9, 26, 10, 56, 46));
        Vehicle vehicle4 = createDummyObject("MACCHINA4", "recharging", 13.2, 16.2, new Date(2017, 8, 24, 9, 37, 22));

        ArrayList<Vehicle> vehicleListToJSon = new ArrayList<Vehicle>();
        vehicleListToJSon.add(vehicle);
        vehicleListToJSon.add(vehicle2);
        vehicleListToJSon.add(vehicle3);
        vehicleListToJSon.add(vehicle4);

        try {
            // Convert object to JSON string and save into a file directly
            mapper.writeValue(new File("D:\\vehicles.json"), vehicleListToJSon);

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(vehicleListToJSon);
            System.out.println(jsonInString);

            // Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vehicleListToJSon);
            System.out.println(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Vehicle getVehicle(String carLicencePlate) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convert JSON string from file to Object
            Vehicle[] vehicleListFromJSon= new Vehicle[10];
            vehicleListFromJSon = mapper.readValue(new File("D:\\vehicles.json"), Vehicle[].class);
            for (int i =0; i < vehicleListFromJSon.length; i++) {
                Vehicle actualVehicle = vehicleListFromJSon[i];
                if(actualVehicle.getCarLicencePlate().equals(carLicencePlate)){
                    System.out.println("Vehicle found! -> " + actualVehicle.getCarLicencePlate() + " " + actualVehicle.getState());
                    return actualVehicle;
                }
            }
            System.out.println("Vehicle not found, retry!");
            return null;

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Vehicle createDummyObject(String carLicencePlate, String state, Double latitude, Double longitude, Date lastUpdate) {
        System.out.println(lastUpdate.toString());
        Vehicle vehicle = new Vehicle(carLicencePlate, state, latitude, longitude, lastUpdate);
        return vehicle;
    }
}
