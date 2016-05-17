package com.jdelorenzo.capstoneproject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * An {@link DialogFragment} subclass for creating and naming a new workout.
 */
public class CreateWorkoutDialogFragment extends DialogFragment {
    interface CreateWorkoutDialogListener {
        void onDialogPositiveClick(String name);
    }

    CreateWorkoutDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.dialog_create_workout, null);
        builder.setTitle(R.string.dialog_create_workout_title)
                .setIcon(R.drawable.run)
                .setMessage(R.string.dialog_name_workout_text)
                .setView(rootView)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText nameEditText = (EditText) rootView.findViewById(R.id.workout_name_edit_text);
                        mListener.onDialogPositiveClick(nameEditText.getText().toString());
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
            mListener = (CreateWorkoutDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement CreateWorkoutDialogListener");
        }
    }
}
