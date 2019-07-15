package com.example.spotifyauthentication;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;


// An interface which a client of this Spinner could use to receive
 // open/closed events for this Spinner.
public class CustomSpinner extends AppCompatSpinner {

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface OnSpinnerEventsListener {

        // callback triggered when the spinner was opened
        void onSpinnerOpened(AppCompatSpinner spinner);

        // callback triggered when the spinner was closed.
        void onSpinnerClosed(AppCompatSpinner spinner);
    }

    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;

    // implement the Spinner constructors that you need

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for when the container holding this Spinner may lose focus
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened(this);
        }
        return super.performClick();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent();
        }
    }

    // Register the listener which will listen for events
    public void setSpinnerEventsListener(
            OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    // Propagate the closed Spinner event to the listener from outside if needed
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onSpinnerClosed(this);
        }
    }

    // A boolean flag indicating that the Spinner triggered an open event
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

}
