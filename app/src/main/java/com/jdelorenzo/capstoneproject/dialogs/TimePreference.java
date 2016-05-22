package com.jdelorenzo.capstoneproject.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import com.jdelorenzo.capstoneproject.R;

import java.util.Locale;

/*
Extension of {@link DialogPreference} that allows the user to select a preferred rest time,
using the {@link RestTimePicker}.
 */

public class TimePreference extends DialogPreference {
    private int lastMinute = 0;
    private int lastSeconds = 0;
    private RestTimePicker picker;
    private AttributeSet mAttributeSet;

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mAttributeSet = attributeSet;
    }

    @Override
    protected View onCreateDialogView() {
        picker = new RestTimePicker(getContext(), mAttributeSet, android.R.attr.preferenceStyle);
        picker.setOnTimeChangedListener(new RestTimePicker.OnRestTimeChangedListener() {
            @Override
            public void onRestTimeChanged(RestTimePicker view, int minute, int seconds) {
                lastMinute = minute;
                lastSeconds = seconds;
            }
        });
        return(picker);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setCurrentMinute(lastMinute);
        picker.setCurrentSecond(lastSeconds);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            lastMinute = picker.getCurrentMinute();
            lastSeconds = picker.getCurrentSeconds();

            int seconds = lastMinute * 60 + lastSeconds;

            if (callChangeListener(seconds)) {
                persistInt(seconds);
                notifyChanged();
            }
        }
        setSummary(getSummary());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (null == defaultValue) defaultValue = 0;
        int time = restorePersistedValue ? getPersistedInt((int)defaultValue) : (int)defaultValue;
        lastMinute = time / 60;
        lastSeconds = time % 60;
        setSummary(getSummary());
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public CharSequence getSummary() {
        if (lastMinute > 0) {
            return String.format(Locale.getDefault(),
                    getContext().getString(R.string.prefs_rest_summary_format_minutes),
                    lastMinute,
                    lastSeconds);
        }
        else {
            return String.format(Locale.getDefault(),
                    getContext().getString(R.string.prefs_rest_summary_format),
                    lastSeconds);
        }
    }
}
