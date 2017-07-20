package com.wedriveu.mobile.tripscheduling.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.location.places.Place;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModel;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModelImpl;

/**
 * Created by Marco on 18/07/2017.
 */
public class SchedulingViewImpl extends Fragment implements SchedulingView, View.OnClickListener {
    private EditText mAddress;
    private Button mScheduleButton;

    public static SchedulingViewImpl newInstance() {
        return new SchedulingViewImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheduling_view, container, false);
        setupUIComponents(view);
        renderView();
        return view;
    }

    private void setupUIComponents(View view){
        mAddress = (EditText) view.findViewById(R.id.destinationAddressText);
        mScheduleButton = (Button) view.findViewById(R.id.scheduleButton);
    }

    @Override
    public void renderView() {
        mScheduleButton.setOnClickListener(this);
        /*mScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        mAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.destinationAddressText) {
            getAddress();
        }
        if(view.getId() == R.id.scheduleButton) {
            startVehicleSearch();
        }
    }
    private void getAddress() {
        SchedulingViewModel viewModel = getViewModel();
        if(viewModel != null ){
            viewModel.startPlaceAutocomplete();
        }
    }

    private void startVehicleSearch() {
        SchedulingViewModel viewModel = getViewModel();
        if (viewModel != null) {
            viewModel.onSearchVehicleButtonClick();
        }
    }
    private SchedulingViewModel getViewModel() {
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            //String viewModelId = getArguments().getString(Constants.VIEW_MODEL_ID);
            //SchedulingViewModel viewModel = (SchedulingViewModel) componentFinder.getViewModel(SchedulingViewModel.TAG);
            SchedulingViewModelImpl viewModel = (SchedulingViewModelImpl) componentFinder.getViewModel(SchedulingViewModel.TAG);
            //return (SchedulingViewModel) componentFinder.getViewModel(SchedulingViewModel.TAG);
            return viewModel;
        }
        return null;
    }

    @Override
    public void renderError(String message) {

        //Status status = PlaceAutocomplete.getStatus(this, data);
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.common_warning)
                .setMessage(message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        /*
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle(R.string.common_warning)
                .setMessage(message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();*/
    }

    @Override
    public void showSelectedAddress(Place address) {
        mAddress.setText(address.getAddress());
    }


}
