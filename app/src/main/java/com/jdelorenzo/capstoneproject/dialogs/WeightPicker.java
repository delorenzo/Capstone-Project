package com.jdelorenzo.capstoneproject.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.jdelorenzo.capstoneproject.R;

/**
 * Custom time picker that allows the user to pick a rest period in minutes and seconds.
 */
public class WeightPicker extends FrameLayout {
    private int mCurrentWeight = 0;
    private int mCurrentWeightFraction = 0;
    private final NumberPicker mWeightPicker;
    private final NumberPicker mWeightFractionPicker;
    private static final String ARG_STATE = "state";
    private static final String ARG_WEIGHT = "weight";
    private static final String ARG_WEIGHT_FRACTION = "weightFraction";

    private static final OnWeightChangedListener NO_OP_CHANGE_LISTENER = new OnWeightChangedListener() {
        @Override
        public void onRestTimeChanged(WeightPicker view, int minute, int seconds) {

        }
    };
    private OnWeightChangedListener mListener;
    public interface OnWeightChangedListener {
        void onRestTimeChanged(WeightPicker view, int minute, int seconds);
    }

    public WeightPicker(Context context) {
        this(context, null);
    }

    public WeightPicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WeightPicker(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        LayoutInflater.from(context).inflate(R.layout.weight_picker, this, true);
        mWeightPicker = (NumberPicker) findViewById(R.id.minute);
        mWeightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCurrentWeight = newVal;
                onWeightChanged();
            }
        });
        mWeightPicker.setMaxValue(999);
        mWeightPicker.setMinValue(0);
        mWeightFractionPicker = (NumberPicker) findViewById(R.id.seconds);
        mWeightFractionPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCurrentWeightFraction = newVal;
                onWeightChanged();
            }
        });
        mWeightFractionPicker.setMaxValue(9);
        mWeightFractionPicker.setMinValue(0);

    }

    public void setOnWeightChangedListener(@NonNull OnWeightChangedListener listener) {
        mListener = listener;
    }


    /**
     * @return The current minute.
     */
    public Integer getCurrentWeight() {
        return mCurrentWeight;
    }

    public void setCurrentWeight(Integer currentWeight) {
        this.mCurrentWeight = currentWeight;
        updateWeightDisplay();
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentWeightFraction() {
        return mCurrentWeightFraction;
    }

    public void setCurrentSecond(Integer currentSecond) {
        this.mCurrentWeightFraction = currentSecond;
        updateSecondsDisplay();
    }

    private void onWeightChanged() {
        mListener.onRestTimeChanged(this, getCurrentWeight(), getCurrentWeightFraction());
    }

    private void updateWeightDisplay() {
        mWeightPicker.setValue(mCurrentWeight);
        mListener.onRestTimeChanged(this, getCurrentWeight(), getCurrentWeightFraction());
    }

    private void updateSecondsDisplay() {
        mWeightFractionPicker.setValue(mCurrentWeightFraction);
        mListener.onRestTimeChanged(this, getCurrentWeightFraction(), getCurrentWeightFraction());
    }


    //save the super state in a Parcelable, allowing us to store the minutes and seconds
    //on screen rotation
    //@see https://stackoverflow.com/questions/3542333/how-to-prevent-custom-views-from-losing-state-across-screen-orientation-changes
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putParcelable(ARG_STATE, super.onSaveInstanceState());
        b.putInt(ARG_WEIGHT, mCurrentWeight);
        b.putInt(ARG_WEIGHT_FRACTION, mCurrentWeightFraction);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle)state;
            mCurrentWeight = bundle.getInt(ARG_WEIGHT);
            mCurrentWeightFraction = bundle.getInt(ARG_WEIGHT_FRACTION);
            state = bundle.getParcelable(ARG_STATE);
        }
        super.onRestoreInstanceState(state);
    }
}
