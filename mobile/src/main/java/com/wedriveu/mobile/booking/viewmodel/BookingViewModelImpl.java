package com.wedriveu.mobile.booking.viewmodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.app.FactoryProvider;
import com.wedriveu.mobile.booking.router.BookingRouter;
import com.wedriveu.mobile.booking.view.*;
import com.wedriveu.mobile.booking.viewmodel.model.PresentationModelFactory;
import com.wedriveu.mobile.booking.viewmodel.model.PresentationModelFactoryImpl;
import com.wedriveu.mobile.booking.viewmodel.model.TravellingMarkerPresentationModel;
import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.service.booking.BookingService;
import com.wedriveu.mobile.service.vehicle.VehicleService;
import com.wedriveu.mobile.store.BookingStore;
import com.wedriveu.mobile.store.StoreFactory;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.store.VehicleStore;
import com.wedriveu.mobile.util.Dates;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingResponse;
import com.wedriveu.shared.rabbitmq.message.CreateBookingResponse;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Constants;

import java.util.Date;

/**
 * The {@linkplain BookingViewModel} implementation.
 *
 * @author Nicola Lasagni on 29/07/2017.
 */
public class BookingViewModelImpl extends Fragment implements BookingViewModel {

    private static final int RENDER_DELAY = 150;

    private BookingRouter mRouter;
    private PresentationModelFactory mPresentationModelFactory;
    private BookingService mBookingService;
    private VehicleService mVehicleService;
    private UserStore mUserStore;
    private BookingStore mBookingStore;
    private VehicleStore mVehicleStore;

    private BookingServiceHandler<Void> mStartHandler;
    private BookingServiceHandler<Void> mStopHandler;
    private BookingServiceHandler<CreateBookingResponse> acceptBookingHandler;
    private BookingServiceHandler<VehicleUpdate> vehicleUpdatesHandler;
    private BookingServiceHandler<CompleteBookingResponse> vehicleArrivedHandler;
    private BookingServiceHandler<Vehicle> vehicleSubstitutionHandler;
    private BookingServiceHandler<EnterVehicleRequest> enterVehicleHandler;
    private BookingServiceHandler<Void> enterVehicleAndUnsubscribeHandler;

    /**
     * New instance of a {@linkplain BookingViewModel}.
     *
     * @return the booking view model
     */
    public static BookingViewModelImpl newInstance() {
        BookingViewModelImpl fragment = new BookingViewModelImpl();
        fragment.setRetainInstance(true);
        return fragment;
    }

    // This deprecated method has been chosen because the minTargetVersion is 19.
    // Switch to minTargetVersion >= 23 in order to use the new onAttach method.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRouter = (BookingRouter) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StoreFactory storeFactory = ((FactoryProvider) getActivity()).provideStoreFactory();
        ServiceFactory serviceFactory = ((FactoryProvider) getActivity()).provideServiceFactory();
        mStartHandler = new StartHandler(this);
        mStopHandler = new StopHandler(this);
        acceptBookingHandler = new AcceptBookingHandler(this);
        vehicleUpdatesHandler = new VehicleUpdateHandler(this);
        vehicleArrivedHandler = new VehicleArrivedHandler(this);
        vehicleSubstitutionHandler = new VehicleSubstitutionHandler(this);
        enterVehicleHandler = new EnterVehicleHandler(this);
        enterVehicleAndUnsubscribeHandler = new EnterVehicleAndUnsubscribeHandler(this);
        mUserStore = storeFactory.createUserStore();
        mPresentationModelFactory = new PresentationModelFactoryImpl(getContext());
        mVehicleStore = storeFactory.createVehicleStore();
        mBookingStore = storeFactory.createBookingStore();
        mBookingService = serviceFactory.createBookingService();
        mVehicleService = serviceFactory.createVehicleService();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                renderBookingSummary();
            }
        }, RENDER_DELAY);
    }

    private void renderBookingSummary() {
        BookingView view = getBookingView(BookingSummaryView.ID);
        if (view != null) {
            User user = mUserStore.getUser();
            Booking updatedBooking = mBookingStore.getBooking();
            Vehicle vehicle = mVehicleStore.getVehicle();
            view.renderView(
                    mPresentationModelFactory.createBookingSummaryPresentationModel(user,
                            vehicle,
                            updatedBooking));
        }
    }

    @Override
    public void onAcceptBookingButtonClick() {
        mRouter.showProgressDialog();
        acceptBookingHandler.refreshReference(this);
        mBookingService.acceptBooking(mUserStore.getUser().getUsername(),
                mBookingStore.getBooking(),
                acceptBookingHandler);
    }

    @Override
    public void onDeclineBookingButtonClick() {
        goToTripScheduling();
    }

    @Override
    public void onEnterVehicleButtonClick() {
        mRouter.showProgressDialog();
        enterVehicleAndUnsubscribeHandler.refreshReference(this);
        mVehicleService.enterVehicleAndUnsubscribe(enterVehicleAndUnsubscribeHandler);
    }

    private void goToTripScheduling() {
        mBookingStore.clear();
        mVehicleStore.clear();
        mStopHandler.refreshReference(this);
        mVehicleService.stop(mStopHandler);
    }

    private void onVehicleServiceStopped() {
        mRouter.goBackToTripScheduling();
    }

    private void onBookingAccepted(ServiceResult<CreateBookingResponse> response) {
        mRouter.dismissProgressDialog();
        if (response.getResult() == null || !response.getResult().isSuccess()) {
            mRouter.showPopOverDialog(response.getErrorMessage());
            goToTripScheduling();
        } else {
            updateVehicle(response.getResult());
            mRouter.showUserTravellingView();
            showBookingSummary(BookingUserTravellingView.ID);
            mStartHandler.refreshReference(this);
            mVehicleService.start(mStartHandler);
        }
    }

    private void showBookingSummary(final String viewTag) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BookingTravellingView view = getBookingTravellingView(viewTag);
                if (view != null) {
                    User user = mUserStore.getUser();
                    Booking updatedBooking = mBookingStore.getBooking();
                    Vehicle vehicle = mVehicleStore.getVehicle();
                    view.renderView(mPresentationModelFactory.createBookingSummaryPresentationModel(user, vehicle, updatedBooking));
                }
            }
        }, RENDER_DELAY);
    }

    private void onVehicleServiceStarted(String errorMessage) {
        if (!TextUtils.isEmpty(errorMessage)) {
            mRouter.showPopOverDialog(errorMessage);
            goToTripScheduling();
        } else {
            subscribeToMainVehicleEvents();
        }
    }

    private void subscribeToMainVehicleEvents() {
        vehicleUpdatesHandler.refreshReference(this);
        enterVehicleHandler.refreshReference(this);
        vehicleSubstitutionHandler.refreshReference(this);
        mVehicleService.subscribeToVehicleUpdates(vehicleUpdatesHandler);
        mVehicleService.subscribeToEnterVehicle(enterVehicleHandler);
        mVehicleService.subscribeToVehicleSubstitution(vehicleSubstitutionHandler);
    }

    private void onVehicleUpdated(ServiceResult<VehicleUpdate> result) {
        VehicleUpdate update = result.getResult();
        Vehicle vehicle = mVehicleStore.getVehicle();
        if (vehicle != null && vehicle.getLicencePlate().equals(update.getLicense())) {
            updateVehicle(update);
            Vehicle updatedVehicle = mVehicleStore.getVehicle();
            if (Constants.Vehicle.STATUS_BROKEN_STOLEN.equals(updatedVehicle.getStatus())) {
                waitForVehicleSubstitution();
            } else {
                showVehicleUpdate(updatedVehicle);
            }
        }
    }

    private void updateVehicle(CreateBookingResponse response) {
        Date arriveAtUser = new Date(response.getUserArrivalTime());
        Date arriveAtDestination = new Date(response.getDestinationArrivalTime());
        Vehicle vehicle = mVehicleStore.getVehicle();
        Vehicle newVehicle =
                new Vehicle(vehicle.getLicencePlate(),
                        vehicle.getStatus(),
                        vehicle.getVehicleName(),
                        vehicle.getDescription(),
                        vehicle.getPictureURL(),
                        Dates.format(arriveAtUser),
                        Dates.format(arriveAtDestination));
        mVehicleStore.storeVehicle(newVehicle);
    }

    private void updateVehicle(VehicleUpdate update) {
        Vehicle vehicle = mVehicleStore.getVehicle();
        vehicle.setStatus(update.getStatus());
        vehicle.setPosition(update.getPosition());
        mVehicleStore.storeVehicle(vehicle);
    }

    private void showVehicleUpdate(Vehicle vehicle) {
        TravellingMarkerPresentationModel presentationModel =
                mPresentationModelFactory.createTravellingMarkerPresentationModel(vehicle);
        String[] tags = {
                BookingUserTravellingView.ID,
                BookingDestinationTravellingView.ID,
                BookingUserSubstitutionTravellingView.ID
        };
        for (String tag : tags) {
            BookingTravellingView view = getBookingTravellingView(tag);
            if (view != null) {
                view.showTravellingMarker(presentationModel);
            }
        }
    }

    private void onEnterVehicleRequest(ServiceResult<EnterVehicleRequest> result) {
        if (result.getResult() == null ||
                !result.getResult().getLicensePlate().equals(mBookingStore.getBooking().getLicensePlate())) {
            mRouter.showPopOverDialog(result.getErrorMessage());
            goToTripScheduling();
        } else {
            mRouter.showEnterVehicleView();
        }
    }

    private void onVehicleEntrance() {
        mRouter.dismissProgressDialog();
        mRouter.showDestinationTravellingView();
        showBookingSummary(BookingDestinationTravellingView.ID);
        mVehicleService.subscribeToVehicleArrived(vehicleArrivedHandler);
    }

    private void onVehicleArrived(ServiceResult<CompleteBookingResponse> result) {
        mVehicleService.unsubscribeToVehicleArrived();
        mVehicleService.unsubscribeToVehicleUpdates();
        mVehicleService.unsubscribeToVehicleSubstitution();
        String message;
        if (result.getResult() == null || !result.getResult().isSuccess()) {
            message = result.getErrorMessage();
        } else {
            message = getString(R.string.arrived_at_destination);
        }
        mRouter.showPopOverDialog(message);
        goToTripScheduling();
    }

    private void waitForVehicleSubstitution() {
        mVehicleService.unsubscribeToVehicleUpdates();
        mVehicleService.unsubscribeToVehicleArrived();
        mVehicleService.unsubscribeToEnterVehicle();
        mRouter.showSubstitutionView();
    }

    private void onSubstitutionVehicleReceived(ServiceResult<Vehicle> result) {
        mVehicleService.unsubscribeToVehicleSubstitution();
        String errorMessage = result.getErrorMessage();
        if (!TextUtils.isEmpty(errorMessage)) {
            mRouter.showPopOverDialog(result.getErrorMessage());
            goToTripScheduling();
        } else {
            substituteVehicle(result.getResult());
        }
    }

    private void substituteVehicle(Vehicle substitutionVehicle) {
        Booking booking = mBookingStore.getBooking();
        if (booking != null) {
            Booking newBooking =
                    new Booking(substitutionVehicle.getLicencePlate(),
                            booking.getDestinationAddress(),
                            booking.getUserPosition(),
                            booking.getDestinationPosition());
            mBookingStore.storeBooking(newBooking);
            mVehicleStore.storeVehicle(substitutionVehicle);
            mRouter.showSubstitutionTravellingView();
            subscribeToMainVehicleEvents();
            showBookingSummary(BookingUserSubstitutionTravellingView.ID);
        }
    }

    private BookingTravellingView getBookingTravellingView(String tag) {
        BookingTravellingView view = null;
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            view = (BookingTravellingView) componentFinder.getView(tag);
        }
        return view;
    }

    private BookingView getBookingView(String tag) {
        BookingView view = null;
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            view = (BookingView) componentFinder.getView(tag);
        }
        return view;
    }

    private static class StartHandler extends BookingServiceHandler<Void>  {

        private StartHandler(BookingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(BookingViewModelImpl viewModel, ServiceResult<Void> result) {
            viewModel.onVehicleServiceStarted(result.getErrorMessage());
        }
    }

    private static class StopHandler extends BookingServiceHandler<Void>  {

        private StopHandler(BookingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(BookingViewModelImpl viewModel, ServiceResult<Void> result) {
            viewModel.onVehicleServiceStopped();
        }
    }


    private static class AcceptBookingHandler extends BookingServiceHandler<CreateBookingResponse> {

        private AcceptBookingHandler(BookingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(BookingViewModelImpl viewModel,
                                     ServiceResult<CreateBookingResponse> result) {
            viewModel.onBookingAccepted(result);
        }
    }

    private static class VehicleUpdateHandler extends BookingServiceHandler<VehicleUpdate> {

        private VehicleUpdateHandler(BookingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(BookingViewModelImpl viewModel,
                                     ServiceResult<VehicleUpdate> result) {
            viewModel.onVehicleUpdated(result);
        }
    }

    private static class VehicleSubstitutionHandler extends BookingServiceHandler<Vehicle> {

        private VehicleSubstitutionHandler(BookingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(BookingViewModelImpl viewModel,
                                     ServiceResult<Vehicle> result) {
            viewModel.onSubstitutionVehicleReceived(result);
        }

    }

    private static class EnterVehicleHandler extends BookingServiceHandler<EnterVehicleRequest> {

        private EnterVehicleHandler(BookingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(BookingViewModelImpl viewModel,
                                     ServiceResult<EnterVehicleRequest> result) {
            viewModel.onEnterVehicleRequest(result);
        }

    }

    private static class EnterVehicleAndUnsubscribeHandler extends BookingServiceHandler<Void> {

        private EnterVehicleAndUnsubscribeHandler(BookingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(BookingViewModelImpl viewModel,
                                     ServiceResult<Void> result) {
            viewModel.onVehicleEntrance();
        }

    }

    private static class VehicleArrivedHandler extends BookingServiceHandler<CompleteBookingResponse> {

        private VehicleArrivedHandler(BookingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(BookingViewModelImpl viewModel,
                                     ServiceResult<CompleteBookingResponse> result) {
            viewModel.onVehicleArrived(result);
        }
    }

}
