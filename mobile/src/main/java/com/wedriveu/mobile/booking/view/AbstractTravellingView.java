package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.model.Vehicle;

/**
 * @author Nicola Lasagni on 21/08/2017.
 */
public abstract class AbstractTravellingView extends Fragment implements TravellingBookingView, OnMapReadyCallback {

    private static final String KEY_MAP_SAVED_STATE = "mapState";

    private MapView mMap;
    private GoogleMap mGoogleMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);
        mMap = (com.google.android.gms.maps.MapView) view.findViewById(R.id.map);
        Bundle mapState = (savedInstanceState != null)
                ? savedInstanceState.getBundle(KEY_MAP_SAVED_STATE): null;
        mMap.onCreate(mapState);
        return view;
    }

    abstract int getLayoutResource();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMap.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapState = new Bundle();
        mMap.onSaveInstanceState(mapState);
        outState.putBundle(KEY_MAP_SAVED_STATE, mapState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    @Override
    public void showVehicle(Vehicle vehicle) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            LatLng latLng = new LatLng(vehicle.getPosition().getLatitude(), vehicle.getPosition().getLongitude());
            String title = vehicle.getVehicleName();
            String subtitle = vehicle.getLicencePlate();
            MarkerOptions markerOptions =
                    new MarkerOptions().position(latLng)
                            .title(title)
                            .snippet(subtitle)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle_blue_marker));
            mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }
}
