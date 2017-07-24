package com.wedriveu.mobile.service.scheduling;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.location.places.Place;
import com.wedriveu.mobile.model.SchedulingLocation;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Marco on 18/07/2017.
 */
public class SchedulingServiceImpl implements SchedulingService {

    private SchedulingServiceApi mSchedulingServiceApi;
    private SchedulingLocation schedulingLocation;

    public SchedulingServiceImpl() {
        schedulingLocation = new SchedulingLocation();
        mSchedulingServiceApi = RetrofitClient.getClient().create(SchedulingServiceApi.class);
    }

    @Override
    public void findNearestVehicle(Place address, final SchedulingServiceCallback callback) {
        schedulingLocation.setDestinationLatitude(address.getLatLng().latitude);
        schedulingLocation.setDestinationLongitude(address.getLatLng().longitude);
        Call<Vehicle> schedulingCall = mSchedulingServiceApi.schedule(schedulingLocation.getUserLatitude(),
                                                              schedulingLocation.getUserLongitude(),
                                                              schedulingLocation.getDestinationLatitude(),
                                                              schedulingLocation.getDestinationLongitude());
        schedulingCall.enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                if (response.isSuccessful()) {
                    final Vehicle res = response.body();
                    Vehicle vehicle = new Vehicle(res.getLicencePlate(),
                                                res.getVehicleName(),
                                                res.getDescription(),
                                                res.getPictureURL(),
                                                res.getArriveAtUserTime(),
                                                res.getArriveAtDestinationTime());
                    callback.onFindNearestVehicleFinished(vehicle, null);
                } else {
                    callback.onFindNearestVehicleFinished(null, response.message());
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                Log.e(TAG, "Failure on vehicle scheduling operation!", t);
                callback.onFindNearestVehicleFinished(null, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onLocationAvailable(Location location) {
        schedulingLocation.setUserLatitude(location.getLatitude());
        schedulingLocation.setUserLongitude(location.getLongitude());
    }

    @Override
    public void onLocationServiceDisabled() {
        schedulingLocation.setUserLongitude(null);
        schedulingLocation.setUserLongitude(null);
    }

}
