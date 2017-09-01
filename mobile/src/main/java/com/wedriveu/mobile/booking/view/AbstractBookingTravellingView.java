package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.booking.viewmodel.model.BookingSummaryPresentationModel;
import com.wedriveu.mobile.booking.viewmodel.model.TravellingMarkerPresentationModel;

/**
 * Abstract class that models the default rendering behaviour of a {@linkplain BookingTravellingView}.
 *
 * @author Nicola Lasagni on 21/08/2017.
 */
public abstract class AbstractBookingTravellingView extends AbstractBookingView implements BookingTravellingView,
                                                                                           OnMapReadyCallback {

    private static final String KEY_MAP_SAVED_STATE = "mapState";
    private static final int MAP_PADDING = 80;

    private MapView mMap;
    private BookingSummaryPresentationModel mPresentationModel;
    private GoogleMap mGoogleMap;
    private Marker mVehicleMarker;

    @Override
    protected void setupViewComponents(View view, Bundle savedInstanceState) {
        mMap = (com.google.android.gms.maps.MapView) view.findViewById(R.id.map);
        Bundle mapState = (savedInstanceState != null)
                ? savedInstanceState.getBundle(KEY_MAP_SAVED_STATE): null;
        mMap.onCreate(mapState);
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
        if (mPresentationModel != null) {
            addMarker(mPresentationModel.getUserMarker());
            addMarker(mPresentationModel.getDestinationMarker());
            addMarker(mPresentationModel.getHeadQuarterMarker());
            mGoogleMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(mPresentationModel.getMapBounds(), MAP_PADDING));
        }
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.app_name;
    }

    @Override
    public void renderPresentationModel(BookingSummaryPresentationModel presentationModel) {
        mPresentationModel = presentationModel;
        mMap.getMapAsync(this);
    }

    @Override
    public void showTravellingMarker(TravellingMarkerPresentationModel presentationModel) {
        if (mGoogleMap != null) {
            if (mVehicleMarker != null) {
                mVehicleMarker.setPosition(presentationModel.getPosition());
            } else {
                mVehicleMarker = addMarker(presentationModel);
            }
        } else {
            mMap.getMapAsync(this);
        }
    }

    private Marker addMarker(TravellingMarkerPresentationModel marker) {
        MarkerOptions markerOptions =
                new MarkerOptions().position(marker.getPosition())
                        .title(marker.getTitle())
                        .snippet(marker.getSubtitle());
        if (marker.hasIconResource()) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(marker.getIconResource()));
        }
        return mGoogleMap.addMarker(markerOptions);
    }
}
