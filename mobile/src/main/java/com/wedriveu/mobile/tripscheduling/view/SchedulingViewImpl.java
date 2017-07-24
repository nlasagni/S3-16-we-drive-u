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

    private EditText mAddressEditText;
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
        mAddressEditText = (EditText) view.findViewById(R.id.destinationAddressText);
        mScheduleButton = (Button) view.findViewById(R.id.scheduleButton);
    }

    @Override
    public void renderView() {
        mScheduleButton.setOnClickListener(this);
        mAddressEditText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.destinationAddressText:
                getAddress();
                break;
            case R.id.scheduleButton:
                if(mAddressEditText.getText().toString().trim().length() == 0) {
                    renderError(getString(R.string.destination_address_not_filled));
                } else {
                    startVehicleSearch();
                }
                break;
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
            SchedulingViewModelImpl viewModel =
                    (SchedulingViewModelImpl) componentFinder.getViewModel(SchedulingViewModel.TAG);
            return viewModel;
        }
        return null;
    }

    @Override
    public void renderError(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.common_warning)
                .setMessage(message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void showSelectedAddress(Place address) {
        mAddressEditText.setText(address.getAddress());
    }

}
