package com.wedriveu.services.vehicle.entity;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Constants;
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

    public VehicleStoreImpl() {
        createJsonFile();
    }

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.NearestControl.AVAILABLE_REQUEST, this::getAllAvailableVehiclesInRange);
        eventBus.consumer(Messages.NearestControl.GET_VEHICLE_NEAREST, msg ->
            getVehicleForNearest(msg, false)
        );
        eventBus.consumer(Messages.BookingControl.GET_VEHICLE_BOOKING, this::getVehicleForBooking);
        eventBus.consumer(Messages.VehicleRegister.REGISTER_VEHICLE_REQUEST, this::addVehicle);
        eventBus.consumer(Messages.VehicleStore.CLEAR_VEHICLES, msg -> deleteAllVehicles());
        eventBus.consumer(Messages.Analytics.GET_VEHICLES_REQUEST, this::getVehicleList);
        eventBus.consumer(Messages.VehicleStore.UPDATE_VEHICLE_STATUS, msg -> {
            VehicleUpdate update = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), VehicleUpdate.class);
            updateVehicleInVehicleList(update.getLicense(), update.getStatus(), update.getPosition(), new Date());
        });
        eventBus.consumer(Messages.VehicleSubstitution.GET_VEHICLE_FOR_SUBSTITUTION, this::getVehicleForSubstitution);
        eventBus.consumer(Messages.VehicleSubstitution.GET_NEAREST_VEHICLE_FOR_SUBSTITUTION, msg ->
           getVehicleForNearest(msg, true)
        );
        eventBus.consumer(Messages.VehicleSubstitution.GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION, msg -> {
            SubstitutionRequest request =
                    VertxJsonMapper.mapTo((JsonObject) msg.body(), SubstitutionRequest.class);
            getSubstitutionAvailableVehicles(request);
        });
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

    private void addVehicle(Message message) {
        JsonObject responseJson = new JsonObject();
        JsonObject vehicleRequesterJson = (JsonObject) message.body();
        Vehicle vehicleRequester = vehicleRequesterJson.mapTo(Vehicle.class);
        boolean success = addVehicle(vehicleRequester);
        responseJson.put(REGISTER_RESULT, success);
        responseJson.put(Messages.Trip.LICENSE_PLATE,
                vehicleRequesterJson.getValue(Messages.Trip.LICENSE_PLATE));
        eventBus.send(Messages.VehicleStore.REGISTER_VEHICLE_COMPLETED, responseJson);
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getLicensePlate() == null) {
            return false;
        } else {
            List<Vehicle> vehicles = getVehicleList();
            Optional existingVehicle = vehicles.stream().filter(x -> x.equals(vehicle)).findFirst();
            if (!existingVehicle.isPresent()) {
                vehicles.add(vehicle);
                writeJsonVehicleFile((ArrayList<Vehicle>) vehicles);
            }
            return !existingVehicle.isPresent();
        }
    }

    @Override
    public Vehicle getVehicle(String licensePlate) {
        List<Vehicle> vehicles = getVehicleList();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getLicensePlate().equals(licensePlate)) {
                return vehicle;
            }
        }
        return null;
    }

    private void getVehicleForNearest(Message message, boolean forSubstitution) {
        JsonObject vehicleData = (JsonObject) message.body();
        String carLicencePlate = vehicleData.getString(Messages.Trip.LICENSE_PLATE);
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
        String carLicencePlate = vehicleData.getString(Messages.Trip.LICENSE_PLATE);
        Vehicle requestedVehicle = getVehicle(carLicencePlate);
        eventBus.send(String.format(Messages.VehicleStore.GET_VEHICLE_COMPLETED_BOOKING, senderId),
                requestedVehicle == null ? null :VertxJsonMapper.mapFrom(requestedVehicle));
    }

    private void getVehicleForSubstitution(Message message) {
        JsonObject vehicleData = (JsonObject) message.body();
        String senderId = vehicleData.getString(Messages.SENDER_ID);
        String carLicencePlate = vehicleData.getString(Messages.Trip.LICENSE_PLATE);
        Vehicle requestedVehicle = getVehicle(carLicencePlate);
        eventBus.send(String.format(Messages.VehicleStore.GET_VEHICLE_FOR_SUBSTITUTION_COMPLETED, senderId),
                requestedVehicle == null ? null : VertxJsonMapper.mapFrom(requestedVehicle));
    }


    @Override
    public List<Vehicle> getAllAvailableVehiclesInRange(Position sourcePosition, double minRange, double maxRange) {
        List<Vehicle> vehicles = getVehicleList();
        List<Vehicle> availableVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            Position vehiclePosition = vehicle.getPosition();
            if (STATUS_AVAILABLE.equals(vehicle.getStatus()) &&
                    vehiclePosition != null &&
                    PositionUtils.isInRange(sourcePosition, vehiclePosition, minRange, maxRange)) {
                availableVehicles.add(vehicle);
            }
        }
        return availableVehicles;
    }

    private void getAllAvailableVehiclesInRange(Message message) {
        UserRequest userData = ((JsonObject) message.body()).mapTo(UserRequest.class);
        Position userPosition = userData.getUserPosition();
        List<Vehicle> availableVehicles =
                getAllAvailableVehiclesInRange(userPosition,
                        Constants.Position.DEFAULT_MIN_RANGE,
                        Constants.Position.DEFAULT_MAX_RANGE);
        userData.setVehicleList(availableVehicles);
        eventBus.send(Messages.VehicleStore.AVAILABLE_COMPLETED, JsonObject.mapFrom(userData));
    }

    private void getSubstitutionAvailableVehicles(SubstitutionRequest request) {
        Position sourcePosition = request.getSubstitutionCheck().getSourcePosition();
        List<Vehicle> availableVehicles =
                getAllAvailableVehiclesInRange(sourcePosition,
                        Constants.Position.DEFAULT_MIN_RANGE,
                        Constants.Position.DEFAULT_SUBSTITUTION_RANGE);
        SubstitutionRequest newRequest =
                new SubstitutionRequest(request.getSubstitutionCheck(),
                        request.getSubstitutionVehicle(),
                        request.getSubstitutionVehicleResponseCanDrive(),
                        availableVehicles);
        eventBus.send(Messages.VehicleStore.GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION_COMPLETED,
                JsonObject.mapFrom(newRequest));
    }

    @Override
    public void deleteAllVehicles() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, new ArrayList<Vehicle>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        eventBus.send(Messages.VehicleStore.CLEAR_VEHICLES_COMPLETED, null);
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
        List<Vehicle> vehicles = getVehicleList();
        AnalyticsVehicleList vehicleList = new AnalyticsVehicleList(vehicles);
        eventBus.send(Messages.VehicleStore.GET_VEHICLE_LIST_COMPLETED,
                VertxJsonMapper.mapInBodyFrom(vehicleList));
    }

    @Override
    public void updateVehicleInVehicleList(String carLicencePlate,
                                           String status,
                                           Position position,
                                           Date lastUpdate) {
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

    private void writeJsonVehicleFile(ArrayList<Vehicle> vehicleListToJSon) {
        ObjectMapper mapper = new ObjectMapper();
        checkDuplicatesAndWriteOnVehiclesDb(vehicleListToJSon, mapper);
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


