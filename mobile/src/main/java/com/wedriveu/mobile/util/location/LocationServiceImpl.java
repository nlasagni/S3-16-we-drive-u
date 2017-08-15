package com.wedriveu.mobile.util.location;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Baldassarri on 19/07/2016.
 */
public class LocationServiceImpl extends LocationCallback implements LocationService,
                                            GoogleApiClient.ConnectionCallbacks,
                                            GoogleApiClient.OnConnectionFailedListener,
                                            ResultCallback<LocationSettingsResult>,
                                            LocationListener {

    private Activity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;
    private LocationRequest mLocationRequest;
    private List<LocationServiceListener> mListeners = new ArrayList<>();
    private static LocationServiceImpl instance = null;

    private LocationServiceImpl(Activity activity) {
        mActivity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static LocationService getInstance(Activity activity) {
        if( instance == null ) {
            instance = new LocationServiceImpl(activity);
        }
        return instance;
    }

    @Override
    public Location getLastKnownLocation() {
        return mLastKnownLocation;
    }

    @Override
    public void checkSettings() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        checkLocationSettings();
    }

    @Override
    public void disableLocationService() {
        Log.i(TAG, "Location service disabled.");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
        }
        mGoogleApiClient.disconnect();
        mLastKnownLocation = null;
        notifyLocationServiceDisabled();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == LocationService.CHECK_SETTINGS_REQUEST) {
            Log.i(TAG, "Settings Change accepted.");
            findLocation();
        } else {
            Log.i(TAG, "Settings Change refused.");
            disableLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LocationService.PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findLocation();
            } else {
                disableLocationService();
            }
        }
    }

    @Override
    public void addLocationListener(LocationServiceListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void removeLocationListener(LocationServiceListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended.");
        disableLocationService();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection failed.");
        disableLocationService();
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                findLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            mActivity,
                            CHECK_SETTINGS_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "Ignored IntentSender.SendIntentException.", e);
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way
                // to fix the settings so we won't show the dialog.
                disableLocationService();
                break;
        }
    }

    private void checkLocationSettings() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LOCATION_REQUEST_PRIORITY);
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true)
                .build();
        LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, request)
                .setResultCallback(this);
    }

    private void findLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mActivity.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
                mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            LocationServices.FusedLocationApi.flushLocations(mGoogleApiClient);
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    private void notifyLocationServiceDisabled() {
        for (LocationServiceListener listener : mListeners) {
            listener.onLocationServiceDisabled();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
        notifyLocationAvailable();
    }

    private void notifyLocationAvailable() {
        for (LocationServiceListener listener : mListeners) {
            listener.onLocationAvailable(mLastKnownLocation);
        }
    }

}
