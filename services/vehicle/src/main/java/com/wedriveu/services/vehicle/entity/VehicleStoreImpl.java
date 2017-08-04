package com.wedriveu.services.vehicle.entity;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.shared.utilities.PositionUtils;
import com.wedriveu.services.vehicle.app.Messages;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wedriveu.services.shared.utilities.Constants.USER_LATITUDE;
import static com.wedriveu.services.shared.utilities.Constants.USER_LONGITUDE;
import static io.vertx.core.json.JsonObject.mapFrom;

/**
 * Created by Michele on 12/07/2017.
 * Marco
 */

public class VehicleStoreImpl extends AbstractVerticle implements VehicleStore {

    private EventBus eventBus;

    @Override
    public void start(Future<Void> future) throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.NearestControl.AVAILABLE_REQUEST, this::getAllAvailableVehiclesInRange);
        eventBus.consumer(Messages.VehicleElection.GET_VEHICLE, this::getVehicle);
        createVehiclesFile();
        future.complete();
    }

    @Override
    public void createVehiclesFile() {
        Vehicle vehicle = createVehicle("MACCHINA1",
                "broken",
                new Position(10.2, 13.2),
                new Date(2017, 11, 30, 12, 37, 43));
        Vehicle vehicle2 = createVehicle("MACCHINA2",
                "available",
                new Position(11.2, 14.2),
                new Date(2017, 10, 28, 11, 43, 12));
        Vehicle vehicle3 = createVehicle("MACCHINA3",
                "busy",
                new Position(15.2, 13.2),
                new Date(2017, 9, 26, 10, 56, 46));
        Vehicle vehicle4 = createVehicle("MACCHINA4",
                "recharging",
                new Position(13.2, 16.2),
                new Date(2017, 8, 24, 9, 37, 22));

        ArrayList<Vehicle> vehicleListToJSon = new ArrayList<Vehicle>();
        vehicleListToJSon.add(vehicle);
        vehicleListToJSon.add(vehicle2);
        vehicleListToJSon.add(vehicle3);
        vehicleListToJSon.add(vehicle4);
        writeJsonVehicleFile(vehicleListToJSon);
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        List<Vehicle> vehicles = getVehicleList();
        vehicles.add(vehicle);
        writeJsonVehicleFile((ArrayList<Vehicle>) vehicles);
    }

    //1
    @Override
    public void getAllAvailableVehiclesInRange(Message message) {
        JsonObject userData = new JsonObject();
        Position userPosition = new Position();
        userPosition.setLatitude(Double.parseDouble(userData.getString(USER_LATITUDE)));
        userPosition.setLongitude(Double.parseDouble(userData.getString(USER_LONGITUDE)));
        List<Vehicle> vehicles = getVehicleList();
        JsonArray jsonArray = new JsonArray();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getState().equals("available") && PositionUtils.isInRange(userPosition, vehicle.getPosition())) {
                jsonArray.add(mapFrom(vehicle));
            }
        }
        eventBus.send(Messages.VehicleStore.AVAILABLE_COMPLETED, jsonArray);
    }

    //2
    @Override
    public void getVehicle(Message message) {
        List<Vehicle> vehicles = getVehicleList();
        JsonObject vehicleData = (JsonObject) message.body();
        String carLicencePlate = vehicleData.getString(Constants.CAR_LICENCE_PLATE);
        /*
        after this the object should contain vehicle, pickuptime, arriveatdestinationtime, username
        * */

        Vehicle vehicle = getRequestedVehicle(vehicles, carLicencePlate);
        vehicleData.remove(Constants.CAR_LICENCE_PLATE);
        vehicleData.put(Constants.ELECTED_VEHICLE, vehicle);
        eventBus.send(Messages.VehicleStore.GET_VEHICLE_COMPLETED, vehicleData);
    }

    @Override
    public List<Vehicle> getVehicleList() {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicles = readFromVehiclesDb(mapper);
        return vehicles;
    }

    @Override
    public void updateVehicleInVehicleList(String carLicencePlate, String state, Position position, Date lastUpdate) {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicles = getVehicleList();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCarLicencePlate().equals(carLicencePlate)) {
                Log.log("Vehicle found for update");
                if (!(vehicle.getState().equals(state))) {
                    vehicle.setState(state);
                }
                if (!(vehicle.getPosition().equals(position))) {
                    vehicle.setPosition(position);
                }
                vehicle.setLastUpdate(lastUpdate);
            }
        }
        checkDuplicatesAndWriteOnVehiclesDb(vehicles, mapper);
    }

    @Override
    public void deleteVehicleFromDb(String carLicencePlate) {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicles = getVehicleList();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCarLicencePlate().equals(carLicencePlate)) {
                Log.log("Vehicle found for delete");
                vehicles.remove(vehicle);
                break;
            }
        }
        checkDuplicatesAndWriteOnVehiclesDb(vehicles, mapper);
    }

    @Override
    public void replaceVehicleInDb(String carLicencePlateToDelete, Vehicle replacementVehicle) {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicles = getVehicleList();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCarLicencePlate().equals(carLicencePlateToDelete)) {
                Log.log("Vehicle found for replacement");
                vehicles.remove(vehicle);
                vehicles.add(replacementVehicle);
                break;
            }
        }
        checkDuplicatesAndWriteOnVehiclesDb(vehicles, mapper);
    }

    @Override
    public boolean theVehicleIsInTheDb(String carLicensePlate) {
        List<Vehicle> vehicles = getVehicleList();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCarLicencePlate().equals(carLicensePlate)) {
                Log.log("Vehicle found");
                return true;
            }
        }
        return false;
    }


    private Vehicle createVehicle(String carLicencePlate,
                                  String state,
                                  Position position,
                                  Date lastUpdate) {

        return new Vehicle(carLicencePlate, state, position, lastUpdate);
    }

    private void writeJsonVehicleFile(ArrayList<Vehicle> vehicleListToJSon) {
        ObjectMapper mapper = new ObjectMapper();
        checkDuplicatesAndWriteOnVehiclesDb(vehicleListToJSon, mapper);
    }

    private Vehicle getRequestedVehicle(List<Vehicle> vehicles, String carLicencePlate) {
        for (Vehicle vehicle : vehicles) {

            if (vehicle.getCarLicencePlate().equals(carLicencePlate)) {
                Log.log("com.wedriveu.services.vehicle.entity.Vehicle found! -> " +
                        vehicle.getCarLicencePlate() +
                        " " +
                        vehicle.getState());
                return vehicle;
            }
        }
        Log.log("com.wedriveu.services.vehicle.entity.Vehicle not found, retry!");
        return null;
    }

    private List<Vehicle> readFromVehiclesDb(ObjectMapper mapper) {
        try {
            List<Vehicle> vehicles =
                    mapper.readValue(new File(Constants.VEHICLES_DATABASE_PATH), new TypeReference<List<Vehicle>>() {
                    });
            return vehicles;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkDuplicatesAndWriteOnVehiclesDb(List<Vehicle> vehicles, ObjectMapper mapper) {
        if (thereAreNoDuplicates(vehicles)) {
            try {
                mapper.writeValue(new File(Constants.VEHICLES_DATABASE_PATH), vehicles);
                String jsonInString = mapper.writeValueAsString(vehicles);
                Log.log(jsonInString);
                jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vehicles);
                Log.log(jsonInString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean thereAreNoDuplicates(List<Vehicle> vehicles) {
        for (int j = 0; j < vehicles.size(); j++) {
            String currentCarLicencePlate = vehicles.get(j).getCarLicencePlate();
            for (int i = 0; i < vehicles.size(); i++) {
                if (j != i
                        && i < vehicles.size() - 1
                        && vehicles.get(i).getCarLicencePlate().equals(currentCarLicencePlate)) {
                    Log.log("There are two duplicates car licence plate: "
                            + vehicles.get(i).getCarLicencePlate()
                            + " "
                            + currentCarLicencePlate);
                    return false;
                }
            }
        }
        Log.log("NO DUPLICATES!");
        return true;
    }

}


