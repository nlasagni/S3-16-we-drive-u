package com.wedriveu.mobile.service.scheduling;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.location.places.Place;
import com.wedriveu.mobile.model.UserLocation;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.RetrofitClient;
import com.wedriveu.mobile.service.scheduling.model.VehicleResponse;
import retrofit2.*;

/**
 * Created by Marco on 18/07/2017.
 */
public class SchedulingServiceImpl implements SchedulingService {

    private SchedulingServiceApi mSchedulingServiceApi;
    private UserLocation userLocation;
    public SchedulingServiceImpl() {
        userLocation = new UserLocation();
        mSchedulingServiceApi = RetrofitClient.getClient().create(SchedulingServiceApi.class);

    }

    @Override
    public void findNearestVehicle(Place address, final SchedulingServiceCallback callback) {
        userLocation.setAddressLatitude(address.getLatLng().latitude);
        userLocation.setAddressLongitude(address.getLatLng().longitude);
        //Vehicle vehicle = new Vehicle("PA02DK", "https://tinyurl.com/y8wfjjnb", "confort and sport", "via cavour, Rome, Italy");
        Log.i("NETWORK_SCHEDULING", userLocation.toString());

        Call<VehicleResponse> loginCall = mSchedulingServiceApi.schedule(userLocation.getUserLatitude(),
                                                              userLocation.getUserLongitude(),
                                                              userLocation.getDestinationLatitude(),
                                                              userLocation.getDestinationLongitude());

        loginCall.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    //TODO Create vehicle
                    final VehicleResponse res = response.body();
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
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                Log.e(TAG, "Failure on login operation!", t);
                callback.onFindNearestVehicleFinished(null, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onLocationAvailable(Location location) {
        userLocation.setGPSLatitude(location.getLatitude());
        userLocation.setGPSLongitude(location.getLongitude());
    }

    @Override
    public void onLocationServiceDisabled() {
        userLocation.setGPSLongitude(null);
        userLocation.setGPSLongitude(null);
    }
}
