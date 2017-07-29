package com.wedriveu.mobile.booking.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.booking.view.BookingView;

/**
 * @author Nicola Lasagni on 29/07/2017.
 */
public class BookingPresenterImpl extends Fragment implements BookingPresenter {

    private String mViewId;

    public static BookingPresenterImpl newInstance(String viewId) {
        BookingPresenterImpl fragment = new BookingPresenterImpl();
        fragment.setRetainInstance(true);
        fragment.setViewId(viewId);
        return fragment;
    }

    private void setViewId(String viewId) {
        mViewId = viewId;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        renderContent();
    }

    private void renderContent() {
        BookingView view = getBookingView();
        if (view != null) {
            //TODO Fill the presentation model to be rendered from the view
            view.renderView(null);
        }
    }

    @Override
    public void onAcceptButtonClick() {
        //TODO Send booking request through dedicated service
    }

    @Override
    public void onDeclineButtonClick() {
        //TODO Go back to trip scheduling
    }

    private BookingView getBookingView() {
        BookingView view = null;
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            view = (BookingView) componentFinder.getView(mViewId);
        }
        return view;
    }

}
