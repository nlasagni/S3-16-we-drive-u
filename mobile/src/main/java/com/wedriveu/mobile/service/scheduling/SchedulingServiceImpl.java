package com.wedriveu.mobile.service.scheduling;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;
import com.google.android.gms.location.places.Place;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.model.UserLocation;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.login.LoginServiceApi;
import com.wedriveu.mobile.util.Constants;
import com.wedriveu.mobile.util.location.LocationManagerListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.net.URL;

/**
 * Created by Marco on 18/07/2017.
 */
public class SchedulingServiceImpl implements SchedulingService, LocationManagerListener {

    private SchedulingServiceApi mSchedulingServiceApi;
    private UserLocation userLocation;
    public SchedulingServiceImpl() {
        userLocation = new UserLocation();
        String baseUrl = Constants.SERVICE_BASE_URL;
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(baseUrl);
        Retrofit retrofit = retrofitBuilder.build();
        mSchedulingServiceApi = retrofit.create(SchedulingServiceApi.class);
    }

    @Override
    public void findNearestVehicle(Place address, final SchedulingServiceCallback callback) {
        userLocation.setAddressLatitude(address.getLatLng().latitude);
        userLocation.setAddressLongitude(address.getLatLng().longitude);
        Vehicle vehicle = new Vehicle("PA02DK", "https://tinyurl.com/y8wfjjnb", "confort and sport", "via cavour, Rome, Italy");
        Log.i("NETWORK_SCHEDULING", userLocation.toString());
        //TODO delete this line afterwards
        callback.onFindNearestVehicleFinished(vehicle, null);
        /*Call<Void> loginCall = mSchedulingServiceApi.schedule(userLocation.getGPSPosition(), userLocation.getDestinationPosition());
        loginCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    //TODO Create vehicle
                    response.body()
                    Vehicle vehicle = new Vehicle("PA02DK", "https://tinyurl.com/y8wfjjnb", "confort and sport", "via cavour, Rome, Italy");
                    callback.onFindNearestVehicleFinished(vehicle, null);
                } else {
                    callback.onFindNearestVehicleFinished(null, response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Failure on login operation!", t);
                callback.onFindNearestVehicleFinished(null, t.getLocalizedMessage());
            }
        });*/
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
