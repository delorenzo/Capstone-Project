package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.jdelorenzo.capstoneproject.R;

import java.io.Serializable;

/*
Extension of {@link DialogFragment} that allows the user to select from the existing workout names.
 */

public class SelectRoutineDialogFragment extends DialogFragment {
    private String[] mRoutines;
    private long[] mRoutineIds;
    private static final String ARG_CALLBACK = "callback";
    private static final String ARG_ROUTINE_NAMES = "routines";
    private static final String ARG_ROUTINE_IDS = "routineIds";

    public interface SelectRoutineListener extends Serializable {
        void onRoutineSelected(long routineId);
    }

    SelectRoutineListener mCallback;

    public static SelectRoutineDialogFragment newInstance(SelectRoutineListener callback, String[] workouts, long[] ids) {
        SelectRoutineDialogFragment selectWorkoutFragment = new SelectRoutineDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_CALLBACK, callback);
        b.putStringArray(ARG_ROUTINE_NAMES, workouts);
        b.putLongArray(ARG_ROUTINE_IDS, ids);
        selectWorkoutFragment.setArguments(b);
        return selectWorkoutFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCallback = (SelectRoutineListener) getArguments().getSerializable(ARG_CALLBACK);
            mRoutines = getArguments().getStringArray(ARG_ROUTINE_NAMES);
            mRoutineIds = getArguments().getLongArray(ARG_ROUTINE_IDS);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(R.string.dialog_select_workout_title)
                .setItems(mRoutines, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onRoutineSelected(mRoutineIds[which]);
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
