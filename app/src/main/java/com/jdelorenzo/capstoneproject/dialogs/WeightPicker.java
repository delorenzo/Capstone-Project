package com.jdelorenzo.capstoneproject.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.Utility;

/**
 * Custom time picker that allows the user to pick a rest period in minutes and seconds.
 */
public class WeightPicker extends FrameLayout {
    private int mWeight = 0;
    private int mWeightFraction = 0;
    private final NumberPicker mWeightPicker;
    private final NumberPicker mWeightFractionPicker;
    private static final String ARG_STATE = "state";
    private static final String ARG_MINUTE = "minute";
    private static final String ARG_SECOND = "second";

    private static final OnWeightChangedListener NO_OP_CHANGE_LISTENER = new OnWeightChangedListener() {
        public void onWeightChanged(WeightPicker view, int weight, int fraction) {

        }
    };
    private OnWeightChangedListener mListener;
    public interface OnWeightChangedListener {
        void onWeightChanged(WeightPicker view, int weight, int fraction);
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
        mWeightPicker = (NumberPicker) findViewById(R.id.weight_number_picker);
        mWeightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mWeight = newVal;
                onWeightChanged();
            }
        });
        mWeightPicker.setMaxValue(999);
        mWeightPicker.setMinValue(0);
        mWeightFractionPicker = (NumberPicker) findViewById(R.id.weight_fraction_number_picker);
        mWeightFractionPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mWeightFraction = newVal;
                onWeightChanged();
            }
        });
        mWeightFractionPicker.setMinValue(0);
        mWeightFractionPicker.setMaxValue(9);
        //mWeightFractionPicker.setDisplayedValues(new String[] {"0", "5"});
        TextView unitsTextView = (TextView)findViewById(R.id.weight_picker_units);
        unitsTextView.setText(Utility.getWeightUnits(context));
    }

    public void setOnWeightChangedListener(@NonNull OnWeightChangedListener listener) {
        mListener = listener;
    }


    /**
     * @return The current minute.
     */
    public Integer getCurrentWeight() {
        return mWeight;
    }

    public void setCurrentWeight(Integer currentWeight) {
        this.mWeight = currentWeight;
        updateWeightDisplay();
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentWeightFraction() {
        return mWeightFraction;
    }

    public void setWeightFraction(Integer currentWeightFraction) {
        this.mWeightFraction = currentWeightFraction;
        updateSecondsDisplay();
    }

    private void onWeightChanged() {
        mListener.onWeightChanged(this, getCurrentWeight(), getCurrentWeightFraction());
    }

    private void updateWeightDisplay() {
        mWeightPicker.setValue(mWeight);
        mListener.onWeightChanged(this, getCurrentWeight(), getCurrentWeightFraction());
    }

    private void updateSecondsDisplay() {
        mWeightFractionPicker.setValue(mWeightFraction);
        mListener.onWeightChanged(this, getCurrentWeight(), getCurrentWeightFraction());
    }


    //save the super state in a Parcelable, allowing us to store the minutes and seconds
    //on screen rotation
    //@see https://stackoverflow.com/questions/3542333/how-to-prevent-custom-views-from-losing-state-across-screen-orientation-changes
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();
        b.putParcelable(ARG_STATE, super.onSaveInstanceState());
        b.putInt(ARG_MINUTE, mWeight);
        b.putInt(ARG_SECOND, mWeightFraction);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle)state;
            mWeight = bundle.getInt(ARG_MINUTE);
            mWeightFraction = bundle.getInt(ARG_SECOND);
            state = bundle.getParcelable(ARG_STATE);
        }
        super.onRestoreInstanceState(state);
    }
}
