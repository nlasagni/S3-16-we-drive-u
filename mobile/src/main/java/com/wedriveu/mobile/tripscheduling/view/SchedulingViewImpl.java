package com.wedriveu.mobile.tripscheduling.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
    private Button mSearchVehicleButton;

    public static SchedulingViewImpl newInstance() {
        return new SchedulingViewImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheduling, container, false);
        setupUIComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        renderView();
    }

    private void setupUIComponents(View view){
        mAddressEditText = (EditText) view.findViewById(R.id.address);
        mSearchVehicleButton = (Button) view.findViewById(R.id.search_vehicle_button);
    }

    @Override
    public void renderView() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.scheduling_title);
        mSearchVehicleButton.setOnClickListener(this);
        mAddressEditText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.address:
                getAddress();
                break;
            case R.id.search_vehicle_button:
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
            return (SchedulingViewModelImpl) componentFinder.getViewModel(SchedulingViewModel.ID);
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
