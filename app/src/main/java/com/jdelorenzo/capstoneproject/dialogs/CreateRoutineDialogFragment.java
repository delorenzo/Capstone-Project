package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.crash.FirebaseCrash;
import com.jdelorenzo.capstoneproject.R;

/**
 * An {@link DialogFragment} subclass for creating and naming a new workout.
 */
public class CreateRoutineDialogFragment extends DialogFragment {
    private static final String LOG_TAG = CreateRoutineDialogFragment.class.getSimpleName();
    public interface CreateRoutineDialogListener {
        void onRoutineCreated(String name);
    }

    CreateRoutineDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.dialog_create_routine, null);
        builder.setTitle(R.string.dialog_create_workout_title)
                .setIcon(R.drawable.ic_run_color_primary_24dp)
                .setMessage(R.string.dialog_name_workout_text)
                .setView(rootView)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText nameEditText = (EditText) rootView.findViewById(R.id.workout_name_edit_text);
                        String name = nameEditText.getText().toString();
                        if (name.isEmpty()) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Empty routine name entered.");
                            nameEditText.setError(getString(R.string.error_message_empty_routine_name));
                            return;
                        }
                        else if (!name.matches(getString(R.string.regex_valid_name))) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Invalid name entered for routine:  " + name);
                            nameEditText.setError(getString(R.string.error_message_invalid_name));
                            return;
                        }
                         mListener.onRoutineCreated(name);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CreateRoutineDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement CreateRoutineDialogListener");
        }
    }
}
