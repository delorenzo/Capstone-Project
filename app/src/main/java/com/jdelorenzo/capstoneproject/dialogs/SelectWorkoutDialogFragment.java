package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.jdelorenzo.capstoneproject.R;

import java.io.Serializable;

public class SelectWorkoutDialogFragment extends DialogFragment {
    private String[] mWorkouts;
    private long[] mWorkoutIds;
    private static final String ARG_CALLBACK = "callback";
    private static final String ARG_WORKOUT_NAMES = "workouts";
    private static final String ARG_WORKOUT_IDS = "workoutIds";

    public interface SelectWorkoutListener extends Serializable {
        void onWorkoutSelected(long workoutId);
    }

    SelectWorkoutListener mCallback;

    public static SelectWorkoutDialogFragment newInstance(SelectWorkoutListener callback, String[] workouts, long[] ids) {
        SelectWorkoutDialogFragment selectWorkoutFragment = new SelectWorkoutDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_CALLBACK, callback);
        b.putStringArray(ARG_WORKOUT_NAMES, workouts);
        b.putLongArray(ARG_WORKOUT_IDS, ids);
        selectWorkoutFragment.setArguments(b);
        return selectWorkoutFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCallback = (SelectWorkoutListener) getArguments().getSerializable(ARG_CALLBACK);
            mWorkouts = getArguments().getStringArray(ARG_WORKOUT_NAMES);
            mWorkoutIds = getArguments().getLongArray(ARG_WORKOUT_IDS);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(R.string.dialog_select_workout_title)
                .setItems(mWorkouts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onWorkoutSelected(mWorkoutIds[which]);
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
