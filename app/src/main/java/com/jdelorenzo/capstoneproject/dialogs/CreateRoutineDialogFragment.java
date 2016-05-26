package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.jdelorenzo.capstoneproject.R;

/**
 * An {@link DialogFragment} subclass for creating and naming a new workout.
 */
public class CreateRoutineDialogFragment extends DialogFragment {
    public interface CreateRoutineDialogListener {
        void onRoutineCreated(String name);
    }

    CreateRoutineDialogListener mListener;
    String[] workouts;
    private static final String ARG_ROUTINES = "routines";

    public static CreateRoutineDialogFragment newInstance(String[] workouts) {
        Bundle b = new Bundle();
        b.putStringArray(ARG_ROUTINES, workouts);
        CreateRoutineDialogFragment fragment = new CreateRoutineDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        workouts = getArguments() != null ? getArguments().getStringArray(ARG_ROUTINES) :
                new String[]{};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.dialog_create_routine, null);
        builder.setTitle(R.string.dialog_create_workout_title)
                .setIcon(R.drawable.run)
                .setMessage(R.string.dialog_name_workout_text)
                .setView(rootView)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText nameEditText = (EditText) rootView.findViewById(R.id.workout_name_edit_text);
                        String name = nameEditText.getText().toString();
                        if (name.isEmpty()) {
                            nameEditText.setError(getString(R.string.error_message_empty_routine_name));
                            return;
                        }
                        for (String workout : workouts) {
                            if (workout.equals(name)) {
                                nameEditText.setError(getString(R.string.error_message_duplicate_routine_name));
                                return;
                            }
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
