package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.jdelorenzo.capstoneproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ModifyWeightDialogFragment extends DialogFragment {
    private Unbinder unbinder;
    @BindView(R.id.weight_number_picker) NumberPicker weightPicker;
    @BindView(R.id.weight_fraction_number_picker) NumberPicker fractionPicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.weight_picker, null);
        unbinder = ButterKnife.bind(this, rootView);
        weightPicker.setMinValue(0);
        weightPicker.setMaxValue(999);
        weightPicker.setEnabled(true);
        weightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.setValue(newVal);
            }
        });
        fractionPicker.setMinValue(0);
        fractionPicker.setMaxValue(9);
        fractionPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.setValue(newVal);
            }
        });
        if (args != null) {
        }
        builder.setTitle(R.string.dialog_edit_exercise_title)
                .setView(rootView)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
