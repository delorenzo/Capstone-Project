package com.jdelorenzo.capstoneproject.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import android.widget.NumberPicker;

import com.jdelorenzo.capstoneproject.R;

import java.util.Locale;

/**
 * Custom time picker that allows you to pick a rest period in minutes and seconds.
 */
public class RestTimePicker extends FrameLayout {
    private int mCurrentMinute = 0;
    private int mCurrentSeconds = 0;
    private final NumberPicker mMinutePicker;
    private final NumberPicker mSecondPicker;
    private static final String ARG_STATE = "state";
    private static final String ARG_MINUTE = "minute";
    private static final String ARG_SECOND = "second";

    private static final OnRestTimeChangedListener NO_OP_CHANGE_LISTENER = new OnRestTimeChangedListener() {
        @Override
        public void onRestTimeChanged(RestTimePicker view, int minute, int seconds) {

        }
    };
    private OnRestTimeChangedListener mListener;
    public interface OnRestTimeChangedListener {
        void onRestTimeChanged(RestTimePicker view, int minute, int seconds);
    }

    public RestTimePicker(Context context) {
        this(context, null);
    }

    public RestTimePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RestTimePicker(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        LayoutInflater.from(context).inflate(R.layout.rest_time_picker, this, true);
        mMinutePicker = (NumberPicker) findViewById(R.id.minute);
        mMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCurrentMinute = newVal;
                onTimeChanged();
            }
        });
        mMinutePicker.setMaxValue(59);
        mMinutePicker.setMinValue(0);
        mMinutePicker.setFormatter(mFormatter);

        mSecondPicker = (NumberPicker) findViewById(R.id.seconds);
        mSecondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCurrentSeconds = newVal;
                onTimeChanged();
            }
        });
        mSecondPicker.setMaxValue(59);
        mSecondPicker.setMinValue(0);
        mSecondPicker.setFormatter(mFormatter);

    }

    public void setOnTimeChangedListener(@NonNull OnRestTimeChangedListener listener) {
        mListener = listener;
    }


    /**
     * @return The current minute.
     */
    public Integer getCurrentMinute() {
        return mCurrentMinute;
    }

    public void setCurrentMinute(Integer currentMinute) {
        this.mCurrentMinute = currentMinute;
        updateMinuteDisplay();
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentSeconds() {
        return mCurrentSeconds;
    }

    public void setCurrentSecond(Integer currentSecond) {
        this.mCurrentSeconds = currentSecond;
        updateSecondsDisplay();
    }

    private void onTimeChanged() {
        mListener.onRestTimeChanged(this, getCurrentMinute(), getCurrentSeconds());
    }

    private void updateMinuteDisplay() {
        mMinutePicker.setValue(mCurrentMinute);
        mListener.onRestTimeChanged(this, getCurrentMinute(), getCurrentSeconds());
    }

    private void updateSecondsDisplay() {
        mSecondPicker.setValue(mCurrentSeconds);
        mListener.onRestTimeChanged(this, getCurrentMinute(), getCurrentSeconds());
    }


    //save the super state in a Parcelable, allowing us to store the minutes and seconds
    //on screen rotation
    //@see https://stackoverflow.com/questions/3542333/how-to-prevent-custom-views-from-losing-state-across-screen-orientation-changes
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putParcelable(ARG_STATE, super.onSaveInstanceState());
        b.putInt(ARG_MINUTE, mCurrentMinute);
        b.putInt(ARG_SECOND, mCurrentSeconds);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle)state;
            mCurrentMinute = bundle.getInt(ARG_MINUTE);
            mCurrentSeconds = bundle.getInt(ARG_SECOND);
            state = bundle.getParcelable(ARG_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    static final NumberPicker.Formatter mFormatter = new NumberPicker.Formatter() {
        @Override
        public String format(int value) {
            return String.format(Locale.getDefault(), "%02d", value);
        }
    };
}
