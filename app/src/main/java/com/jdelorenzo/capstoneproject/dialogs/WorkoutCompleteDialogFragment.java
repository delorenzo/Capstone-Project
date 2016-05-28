package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.jdelorenzo.capstoneproject.R;

public class WorkoutCompleteDialogFragment extends DialogFragment {
    private WorkoutCompleteListener mListener;
    private static final String ARG_DAY_ID = "day_id";

    public interface WorkoutCompleteListener {
        void onWorkoutComplete(long dayId);
    }

    public static WorkoutCompleteDialogFragment newInstance(long id) {
        Bundle b = new Bundle();
        b.putLong(ARG_DAY_ID, id);
        WorkoutCompleteDialogFragment fragment = new WorkoutCompleteDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final long dayId = args != null ? args.getLong(ARG_DAY_ID, 0) : 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_workout_complete_title)
                .setMessage(R.string.dialog_workout_complete_text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onWorkoutComplete(dayId);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (WorkoutCompleteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement WorkoutCompleteListener");
        }
    }
}
