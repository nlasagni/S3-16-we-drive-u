package com.wedriveu.services.vehicle.entity;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Position;
import com.wedriveu.shared.util.PositionUtils;
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

import static com.wedriveu.services.vehicle.rabbitmq.Constants.REGISTER_RESULT;
import static com.wedriveu.shared.util.Constants.VEHICLE;
import static com.wedriveu.shared.util.Constants.Vehicle.LICENSE_PLATE;
import static com.wedriveu.shared.util.Constants.Vehicle.STATUS_AVAILABLE;


/**
 * @author Marco Baldassarri on 12/07/2017.
 * @author Michele
 * @author Nicola Lasagni
 */

public class VehicleStoreImpl extends AbstractVerticle implements VehicleStore {

    private static final String VEHICLES_DATABASE_FILENAME = "vehicles.json";
    private static final String STORE_FOLDER = "store";
    private EventBus eventBus;
    private File file;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.NearestControl.AVAILABLE_REQUEST, this::getAllAvailableVehiclesInRange);
        eventBus.consumer(Messages.NearestControl.GET_VEHICLE_NEAREST, msg ->
            getVehicleForNearest(msg, false)
        );
        eventBus.consumer(Messages.BookingControl.GET_VEHICLE_BOOKING, this::getVehicleForBooking);
        eventBus.consumer(Messages.VehicleRegister.REGISTER_VEHICLE_REQUEST, this::addVehicle);
        eventBus.consumer(Messages.VehicleStore.CLEAR_VEHICLES, msg -> clearVehicles());
        eventBus.consumer(Messages.Analytics.GET_VEHICLES_REQUEST, this::getVehicleList);
        eventBus.consumer(Messages.VehicleStore.UPDATE_VEHICLE_STATUS, msg -> {
            UpdateToService update = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), UpdateToService.class);
            updateVehicleInVehicleList(update.getLicense(), update.getStatus(), update.getPosition(), new Date());
        });
        eventBus.consumer(Messages.VehicleSubstitution.GET_VEHICLE_FOR_SUBSTITUTION, this::getVehicleForSubstitution);
        eventBus.consumer(Messages.VehicleSubstitution.GET_NEAREST_VEHICLE_FOR_SUBSTITUTION, msg ->
           getVehicleForNearest(msg, true)
        );
        eventBus.consumer(Messages.VehicleSubstitution.GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION, msg -> {
            SubstitutionRequest request =
                    VertxJsonMapper.mapTo((JsonObject) msg.body(), SubstitutionRequest.class);
            findSubstitutionVehicle(request);
        });
        createJsonFile();
    }

    private void createJsonFile() {
        try {
            new File(STORE_FOLDER).mkdir();
            file = new File(STORE_FOLDER + File.separator + VEHICLES_DATABASE_FILENAME);
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
        if (vehicleRequester == null || vehicleRequester.getLicensePlate() == null) {
            responseJson.put(REGISTER_RESULT, false);
        } else {
            List<Vehicle> vehicles = getVehicleList();
            responseJson.put(LICENSE_PLATE, vehicleRequesterJson.getValue(LICENSE_PLATE));
            Optional existingVehicle = vehicles.stream().filter(x -> x.equals(vehicleRequester)).findFirst();
            if (existingVehicle.isPresent()) {
                responseJson.put(REGISTER_RESULT, false);
            } else {
                responseJson.put(REGISTER_RESULT, true);
                vehicles.add(vehicleRequester);
                writeJsonVehicleFile((ArrayList<Vehicle>) vehicles);
            }
        }
        eventBus.send(Messages.VehicleStore.REGISTER_VEHICLE_COMPLETED, responseJson);
    }

    @Override
    public Vehicle getVehicle(String licensePlate) {
        List<Vehicle> vehicles = getVehicleList();
        return getRequestedVehicle(vehicles, licensePlate);
    }

    @Override
    public void clearVehicles() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, new ArrayList<Vehicle>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        eventBus.send(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, null);
    }

    @Override
    public void getAllAvailableVehiclesInRange(Message message) {
        UserRequest userData = ((JsonObject) message.body()).mapTo(UserRequest.class);
        Position userPosition = userData.getUserPosition();
        List<Vehicle> vehicles = getVehicleList();
        List<Vehicle> availableVehicles = new ArrayList<>(vehicles.size());
        for (Vehicle vehicle : vehicles) {
            Position vehiclePosition = vehicle.getPosition();
            if (STATUS_AVAILABLE.equals(vehicle.getStatus()) &&
                    vehiclePosition != null &&
                    PositionUtils.isInRange(userPosition, vehiclePosition)) {
                availableVehicles.add(vehicle);
            }
        }
        userData.setVehicleList(availableVehicles);
        eventBus.send(Messages.VehicleStore.AVAILABLE_COMPLETED, JsonObject.mapFrom(userData));
    }

    private void getVehicleForNearest(Message message, boolean forSubstitution) {
        JsonObject vehicleData = (JsonObject) message.body();
        String carLicencePlate = vehicleData.getString(LICENSE_PLATE);
        Vehicle requestedVehicle = getVehicle(carLicencePlate);
        vehicleData.put(VEHICLE, JsonObject.mapFrom(requestedVehicle).toString());
        String address = Messages.VehicleStore.GET_VEHICLE_COMPLETED_NEAREST;
        if (forSubstitution) {
            address = Messages.VehicleStore.GET_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED;
        }
        eventBus.send(address, vehicleData);
    }

    private void getVehicleForBooking(Message message) {
        JsonObject vehicleData = (JsonObject) message.body();
        String senderId = vehicleData.getString(Messages.SENDER_ID);
        String carLicencePlate = vehicleData.getString(LICENSE_PLATE);
        Vehicle requestedVehicle = getVehicle(carLicencePlate);
        eventBus.send(String.format(Messages.VehicleStore.GET_VEHICLE_COMPLETED_BOOKING, senderId),
                VertxJsonMapper.mapFrom(requestedVehicle));
    }

    private void getVehicleForSubstitution(Message message) {
        JsonObject vehicleData = (JsonObject) message.body();
        String senderId = vehicleData.getString(Messages.SENDER_ID);
        String carLicencePlate = vehicleData.getString(LICENSE_PLATE);
        Vehicle requestedVehicle = getVehicle(carLicencePlate);
        eventBus.send(String.format(Messages.VehicleStore.GET_VEHICLE_FOR_SUBSTITUTION_COMPLETED, senderId),
                requestedVehicle == null ? null : VertxJsonMapper.mapFrom(requestedVehicle));
    }

    private void findSubstitutionVehicle(SubstitutionRequest request) {
        Position sourcePosition = request.getSubstitutionCheck().getSourcePosition();
        List<Vehicle> vehicles = getVehicleList();
        List<Vehicle> availableVehicles = new ArrayList<>(vehicles.size());
        for (Vehicle vehicle : vehicles) {
            Position vehiclePosition = vehicle.getPosition();
            if (STATUS_AVAILABLE.equals(vehicle.getStatus()) &&
                    vehiclePosition != null &&
                    PositionUtils.isInSubstitutionRange(sourcePosition, vehiclePosition)) {
                availableVehicles.add(vehicle);
            }
        }
        SubstitutionRequest newRequest =
                new SubstitutionRequest(request.getSubstitutionCheck(),
                        request.getSubstitutionVehicle(),
                        request.getSubstitutionVehicleResponseCanDrive(),
                        availableVehicles);
        eventBus.send(Messages.VehicleStore.GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION_COMPLETED,
                JsonObject.mapFrom(newRequest));
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

    private void getVehicleList(Message message) {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicles = readFromVehiclesDb(mapper);
        if (vehicles == null) {
            vehicles = new ArrayList<>();
        }
        AnalyticsVehicleList vehicleList = new AnalyticsVehicleList(vehicles);
        eventBus.send(Messages.VehicleStore.GET_VEHICLE_LIST_COMPLETED,
                VertxJsonMapper.mapInBodyFrom(vehicleList));
    }

    @Override
    public void updateVehicleInVehicleList(String carLicencePlate, String status, Position position, Date lastUpdate) {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicles = getVehicleList();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getLicensePlate().equals(carLicencePlate)) {
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
            if (vehicle.getLicensePlate().equals(carLicencePlate)) {
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
            if (vehicle.getLicensePlate().equals(carLicencePlateToDelete)) {
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
            if (vehicle.getLicensePlate().equals(carLicensePlate)) {
                return true;
            }
        }
        return false;
    }

    private void writeJsonVehicleFile(ArrayList<Vehicle> vehicleListToJSon) {
        ObjectMapper mapper = new ObjectMapper();
        checkDuplicatesAndWriteOnVehiclesDb(vehicleListToJSon, mapper);
    }

    private Vehicle getRequestedVehicle(List<Vehicle> vehicles, String carLicencePlate) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getLicensePlate().equals(carLicencePlate)) {
                return vehicle;
            }
        }
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean thereAreNoDuplicates(List<Vehicle> vehicles) {
        for (int j = 0; j < vehicles.size(); j++) {
            String currentCarLicencePlate = vehicles.get(j).getLicensePlate();
            for (int i = 0; i < vehicles.size(); i++) {
                if (j != i
                        && i < vehicles.size() - 1
                        && vehicles.get(i).getLicensePlate().equals(currentCarLicencePlate)) {
                    return false;
                }
            }
        }
        return true;
    }

}


