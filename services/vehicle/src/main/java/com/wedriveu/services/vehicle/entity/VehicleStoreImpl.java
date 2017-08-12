package com.wedriveu.services.vehicle.entity;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.shared.utilities.PositionUtils;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.services.vehicle.rabbitmq.UserRequest;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * Created by Michele on 12/07/2017.
 * Marco
 */

public class VehicleStoreImpl extends AbstractVerticle implements VehicleStore {

    private static final String STORE_FOLDER = "store";
    private EventBus eventBus;
    private File file;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.NearestControl.AVAILABLE_REQUEST, this::getAllAvailableVehiclesInRange);
        eventBus.consumer(Messages.NearestControl.GET_VEHICLE, this::getVehicle);
        eventBus.consumer(Messages.VehicleRegister.REGISTER_VEHICLE_REQUEST, this::addVehicle);
        createJsonFile();
    }

    private void createJsonFile() {
        try {
            new File(STORE_FOLDER).mkdir();
            file = new File(STORE_FOLDER + File.separator + Constants.VEHICLES_DATABASE_FILENAME);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addVehicle(Message message) {
        JsonObject responseJson = new JsonObject();
        JsonObject vehicleRequesterJson = (JsonObject) message.body();
        Vehicle vehicleRequester = vehicleRequesterJson.mapTo(Vehicle.class);
        List<Vehicle> vehicles = getVehicleList();
        responseJson.put(CAR_LICENCE_PLATE, vehicleRequesterJson.getValue(CAR_LICENCE_PLATE));
        Optional existingVehicle = vehicles.stream().filter(x -> x.equals(vehicleRequester)).findFirst();
        if (existingVehicle.isPresent()) {
            responseJson.put(REGISTER_RESULT, false);
        } else {
            responseJson.put(REGISTER_RESULT, true);
            vehicles.add(vehicleRequester);
            writeJsonVehicleFile((ArrayList<Vehicle>) vehicles);
        }
        eventBus.send(Messages.VehicleStore.REGISTER_VEHICLE_COMPLETED, responseJson);
    }


    @Override
    public void getAllAvailableVehiclesInRange(Message message) {
        UserRequest userData = ((JsonObject) message.body()).mapTo(UserRequest.class);
        Position userPosition = userData.getUserPosition();
        List<Vehicle> vehicles = getVehicleList();
        List<Vehicle> availableVehicles = new ArrayList<>(vehicles.size());
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getStatus().equals(STATUS_AVAILABLE)
                    && PositionUtils.isInRange(userPosition, vehicle.getPosition())) {
                availableVehicles.add(vehicle);
            }
        }
        userData.setVehicleList(availableVehicles);
        eventBus.send(Messages.VehicleStore.AVAILABLE_COMPLETED, JsonObject.mapFrom(userData));
    }


    @Override
    public void getVehicle(Message message) {
        List<Vehicle> vehicles = getVehicleList();
        JsonObject vehicleData = (JsonObject) message.body();
        String carLicencePlate = vehicleData.getString(Constants.CAR_LICENCE_PLATE);
        JsonObject response = vehicleData.mapFrom(getRequestedVehicle(vehicles, carLicencePlate));
        eventBus.send(Messages.VehicleStore.GET_VEHICLE_COMPLETED, response);
    }

    @Override
    public List<Vehicle> getVehicleList() {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicles = readFromVehiclesDb(mapper);
        if (vehicles != null) {
            return vehicles;
        }
        return new ArrayList<>();
    }

    @Override
    public void updateVehicleInVehicleList(String carLicencePlate, String status, Position position, Date lastUpdate) {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicles = getVehicleList();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCarLicencePlate().equals(carLicencePlate)) {
                Log.log("Vehicle found for update");
                if (!(vehicle.getStatus().equals(status))) {
                    vehicle.setStatus(status);
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
                        vehicle.getStatus());
                return vehicle;
            }
        }
        Log.log("com.wedriveu.services.vehicle.entity.Vehicle not found, retry!");
        return null;
    }

    private List<Vehicle> readFromVehiclesDb(ObjectMapper mapper) {
        try {
            List<Vehicle> vehicles =
                    mapper.readValue(file, new TypeReference<List<Vehicle>>() {
                    });
            return vehicles;
        } catch (IOException e) {
            return null;
        }
    }

    private void checkDuplicatesAndWriteOnVehiclesDb(List<Vehicle> vehicles, ObjectMapper mapper) {
        if (thereAreNoDuplicates(vehicles)) {
            try {
                mapper.writeValue(file, vehicles);
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


