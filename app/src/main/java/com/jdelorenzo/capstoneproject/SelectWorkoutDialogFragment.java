package com.jdelorenzo.capstoneproject;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import java.io.Serializable;

public class SelectWorkoutDialogFragment extends DialogFragment {
    private String[] mWorkouts;
    private long[] mWorkoutIds;
    private static final String ARG_CALLBACK = "callback";
    private static final String ARG_WORKOUT_NAMES = "workouts";
    private static final String ARG_WORKOUT_IDS = "workoutIds";


    interface SelectWorkoutListener extends Serializable {
        void onWorkoutSelected(long workoutId);
    }

    SelectWorkoutListener mListener;

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
            mListener = (SelectWorkoutListener) getArguments().getSerializable(ARG_CALLBACK);
            mWorkouts = getArguments().getStringArray(ARG_WORKOUT_NAMES);
            mWorkoutIds = getArguments().getLongArray(ARG_WORKOUT_IDS);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(R.string.dialog_create_workout_title)
                .setIcon(R.drawable.run)
                .setMessage(R.string.dialog_name_workout)
                .setItems(mWorkouts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onWorkoutSelected(mWorkoutIds[which]);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (null == mListener) {
                mListener = (SelectWorkoutListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement SelectWorkoutListener");
        }
    }
}
