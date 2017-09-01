package com.wedriveu.mobile.booking.viewmodel.model;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;

/**
 * The {@linkplain PresentationModelFactory} implementation.
 *
 * @author Nicola Lasagni on 28/08/2017.
 */
public class PresentationModelFactoryImpl implements PresentationModelFactory {

    private Context mContext;

    /**
     * Instantiates a new PresentationModelFactory.
     *
     * @param context the context needed to retrieve the data to be displayed
     */
    public PresentationModelFactoryImpl(Context context) {
        mContext = context;
    }

    @Override
    public BookingSummaryPresentationModel createBookingSummaryPresentationModel(User user,
                                                                                 Vehicle vehicle,
                                                                                 Booking booking) {
        BookingSummaryPresentationModel presentationModel = new BookingSummaryPresentationModel();
        presentationModel.setVehicleName(vehicle.getVehicleName());
        presentationModel.setLicensePlate(mContext.getString(R.string.vehicle_license_plate, vehicle.getLicencePlate()));
        presentationModel.setDescription(vehicle.getDescription());
        presentationModel.setImageUrl(vehicle.getPictureURL());
        presentationModel.setPickUpTime(mContext.getString(R.string.vehicle_pickup_time, vehicle.getArriveAtUserTime()));
        presentationModel.setArriveTime(mContext.getString(R.string.vehicle_arrives_at, vehicle.getArriveAtDestinationTime()));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        TravellingMarkerPresentationModel userMarker =
                createMarkerAndAddToBounds(user.getUsername(), null, booking.getUserPosition(), builder);
        TravellingMarkerPresentationModel destinationMarker =
                createMarkerAndAddToBounds(booking.getDestinationAddress(), null, booking.getDestinationPosition(), builder);
        TravellingMarkerPresentationModel headQuarterMarker =
                createMarkerAndAddToBounds(mContext.getString(R.string.vehicles_head_quarter), null, Constants.HEAD_QUARTER, builder);
        presentationModel.setUserMarker(userMarker);
        presentationModel.setDestinationMarker(destinationMarker);
        presentationModel.setHeadQuarterMarker(headQuarterMarker);
        presentationModel.setMapBounds(builder.build());
        return presentationModel;
    }

    @Override
    public TravellingMarkerPresentationModel createTravellingMarkerPresentationModel(Vehicle vehicle) {
        return createMarkerAndAddToBounds(vehicle.getLicencePlate(), vehicle.getVehicleName() , vehicle.getPosition());
    }

    private TravellingMarkerPresentationModel createMarkerAndAddToBounds(String title,
                                                                         String subtitle,
                                                                         Position position) {
        TravellingMarkerPresentationModel presentationModel =
                createMarkerAndAddToBounds(title, subtitle, position, null);
        presentationModel.setIconResource(R.drawable.vehicle_blue_marker);
        return presentationModel;
    }

    private TravellingMarkerPresentationModel createMarkerAndAddToBounds(String title,
                                                                         String subtitle,
                                                                         Position position,
                                                                         LatLngBounds.Builder builder) {
        TravellingMarkerPresentationModel vehicleMarker = new TravellingMarkerPresentationModel();
        vehicleMarker.setTitle(title);
        vehicleMarker.setSubtitle(subtitle);
        LatLng latLng = createLatLng(position);
        vehicleMarker.setPosition(latLng);
        if (builder != null) {
            builder.include(latLng);
        }
        return vehicleMarker;
    }

    private LatLng createLatLng(Position position) {
        return new LatLng(position.getLatitude(), position.getLongitude());
    }

}
