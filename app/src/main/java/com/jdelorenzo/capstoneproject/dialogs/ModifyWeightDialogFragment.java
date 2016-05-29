package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ModifyWeightDialogFragment extends DialogFragment {
    private Unbinder unbinder;
    @BindView(R.id.weight_number_picker) NumberPicker weightPicker;
    @BindView(R.id.weight_fraction_number_picker) NumberPicker fractionPicker;
    private int currentWeight;
    private int currentWeightFraction;
    private long exerciseId;
    private ModifyWeightListener mListener;
    private static final String ARG_ID = "id";
    private static final String ARG_WEIGHT = "weight";

    public interface ModifyWeightListener {
        void onWeightModified(long id, double weight);
    }

    public static ModifyWeightDialogFragment newInstance(long id, double weight) {
        Bundle b = new Bundle();
        b.putLong(ARG_ID, id);
        b.putDouble(ARG_WEIGHT, weight);
        ModifyWeightDialogFragment fragment = new ModifyWeightDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ModifyWeightListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement SelectDaysListener");

        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            exerciseId = args.getLong(ARG_ID);
            String weightString = Utility.getFormattedWeightStringWithoutUnits(getActivity(),
                    args.getDouble(ARG_WEIGHT));
            String[] weightParts = weightString.split("\\.");
            if (weightParts.length == 2) {
                currentWeight = Integer.parseInt(weightParts[0]);
                currentWeightFraction = Integer.parseInt(weightParts[1]);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.weight_picker, null);
        WeightPicker picker = new WeightPicker(getActivity());
        picker.setOnWeightChangedListener(new WeightPicker.OnWeightChangedListener() {
            @Override
            public void onWeightChanged(WeightPicker view, int weight, int fraction) {
                currentWeight = weight;
                currentWeightFraction = fraction;
            }
        });
        picker.setCurrentWeight(currentWeight);
        picker.setWeightFraction(currentWeightFraction);
        unbinder = ButterKnife.bind(this, rootView);
        if (args != null) {
        }
        builder.setTitle(R.string.dialog_edit_exercise_title)
                .setView(picker)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String weightString = currentWeight + "." + currentWeightFraction;
                        mListener.onWeightModified(exerciseId, Double.parseDouble(weightString));
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
