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
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.Utility;
import com.jdelorenzo.capstoneproject.model.Exercise;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/*
This fragment is used to create a new exercise.
The CreateExerciseDialogFragmentListener callback can be eithehr passed in on newInstance() or
implemented in the calling activity.
 */
public class EditExerciseDialogFragment extends DialogFragment {
    private static final String ARG_CALLBACK = "callback";
    @BindView(R.id.exercise) EditText exerciseEditText;
    @BindView(R.id.sets) EditText setsEditText;
    @BindView(R.id.repetitions) EditText repetitionsEditText;
    @BindView(R.id.weight) EditText weightEditText;
    @BindView(R.id.weight_units) TextView weightUnits;

    private Unbinder unbinder;
    public interface EditExerciseDialogFragmentListener extends Serializable {
        void onEditExercise(int sets, int reps, String description, double weight);
    }

    private EditExerciseDialogFragmentListener mCallback;

    public static EditExerciseDialogFragment newInstance(EditExerciseDialogFragmentListener callback) {
        Bundle b = new Bundle();
        b.putSerializable(ARG_CALLBACK, callback);
        EditExerciseDialogFragment fragment = new EditExerciseDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mCallback = (EditExerciseDialogFragmentListener) getArguments().getSerializable(ARG_CALLBACK);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.dialog_add_exercise, null);
        unbinder = ButterKnife.bind(this, rootView);
        weightUnits.setText(Utility.getWeightUnits(getActivity()));
        builder.setTitle(R.string.dialog_edit_exercise_title)
                .setView(rootView)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String description = exerciseEditText.getText().toString();
                        if (description.isEmpty()) {
                            exerciseEditText.setError(getActivity().getString(R.string.error_message_exercise_name));
                            return;
                        }
                        String setText = setsEditText.getText().toString();
                        if (setText.isEmpty()) {
                            setsEditText.setError(getActivity().getString(R.string.error_message_sets));
                            return;
                        }
                        int sets = Integer.parseInt(setText);
                        String repetitionText = repetitionsEditText.getText().toString();
                        if (repetitionText.isEmpty()) {
                            repetitionsEditText.setError(getActivity().getString(R.string.error_message_repetitions));
                            return;
                        }
                        int repetitions = Integer.parseInt(repetitionText);
                        String weightText = weightEditText.getText().toString();
                        double weight = weightText.isEmpty() ? 0 : Double.parseDouble(weightText);
                        mCallback.onEditExercise(sets, repetitions, description, weight);
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
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments() != null) {
            mCallback = (EditExerciseDialogFragmentListener) getArguments().getSerializable(ARG_CALLBACK);
        }
        try {
            if (null == mCallback) {
                mCallback = (EditExerciseDialogFragmentListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement CreateExerciseDialogFragmentListener");
        }
    }
}
