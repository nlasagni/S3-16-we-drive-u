package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wedriveu.mobile.booking.viewmodel.model.BookingSummaryPresentationModel;

/**
 * Abstract class that models the default rendering behaviour of a {@linkplain BookingView}.
 *
 * @author Nicola Lasagni on 28/08/2017.
 */
public abstract class AbstractBookingView extends Fragment implements BookingView {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);
        setupViewComponents(view, savedInstanceState);
        return view;
    }

    /**
     * Gets layout resource.
     *
     * @return the layout resource
     */
    protected abstract int getLayoutResource();

    /**
     * Sets view components.
     *
     * @param view               the view
     * @param savedInstanceState the saved instance state
     */
    protected abstract void setupViewComponents(View view, Bundle savedInstanceState);

    /**
     *
     * @return
     */
    protected abstract @StringRes int getActionBarTitle();

    protected abstract void renderPresentationModel(BookingSummaryPresentationModel presentationModel);

    @Override
    public void renderView(BookingSummaryPresentationModel presentationModel) {
        setActionBarTitle();
        renderPresentationModel(presentationModel);
    }

    private void setActionBarTitle() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getActionBarTitle());
        }
    }

}
