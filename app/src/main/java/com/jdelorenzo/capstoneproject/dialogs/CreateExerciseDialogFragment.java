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
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.Utility;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/*
This fragment is used to create a new exercise.
The CreateExerciseDialogFragmentListener callback can be eithehr passed in on newInstance() or
implemented in the calling activity.
 */
public class CreateExerciseDialogFragment extends DialogFragment {
    private static final String ARG_CALLBACK = "callback";
    @BindView(R.id.exercise) EditText exerciseEditText;
    @BindView(R.id.sets) EditText setsEditText;
    @BindView(R.id.repetitions) EditText repetitionsEditText;
    @BindView(R.id.weight) EditText weightEditText;
    @BindView(R.id.weight_units) TextView weightUnits;
    private static final String LOG_TAG = CreateExerciseDialogFragment.class.getSimpleName();

    private Unbinder unbinder;
    public interface CreateExerciseDialogFragmentListener extends Serializable {
        void onCreateExercise(int sets, int reps, String description, double weight);
    }

    private CreateExerciseDialogFragmentListener mCallback;

    public static CreateExerciseDialogFragment newInstance(CreateExerciseDialogFragmentListener callback) {
        Bundle b = new Bundle();
        b.putSerializable(ARG_CALLBACK, callback);
        CreateExerciseDialogFragment fragment = new CreateExerciseDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mCallback = (CreateExerciseDialogFragmentListener) getArguments().getSerializable(ARG_CALLBACK);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.dialog_add_exercise, null);
        unbinder = ButterKnife.bind(this, rootView);
        weightUnits.setText(Utility.getWeightUnits(getActivity()));
        builder.setTitle(R.string.dialog_create_exercise_title)
                .setIcon(R.drawable.ic_run_color_primary_24dp)
                .setMessage(R.string.dialog_create_exercise_text)
                .setView(rootView)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String description = exerciseEditText.getText().toString();
                        if (description.isEmpty()) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Empty description entered");
                            exerciseEditText.setError(getActivity().getString(R.string.error_message_exercise_name));
                            return;
                        }
                        else if (!description.matches(getString(R.string.regex_valid_name))) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Invalid exercise name " + description + " entered");
                            exerciseEditText.setError(getString(R.string.error_message_invalid_name));
                            return;
                        }
                        String setText = setsEditText.getText().toString();
                        if (setText.isEmpty()) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Empty sets entered");
                            setsEditText.setError(getActivity().getString(R.string.error_message_sets));
                            return;
                        }
                        else if (!setText.matches(getString(R.string.regex_valid_digit))) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Invalid set amount " + setText + " entered");
                            setsEditText.setError(getString(R.string.error_message_invalid_digit));
                            return;
                        }
                        int sets = Integer.parseInt(setText);
                        String repetitionText = repetitionsEditText.getText().toString();
                        if (repetitionText.isEmpty()) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Empty reps entered");
                            repetitionsEditText.setError(getActivity().getString(R.string.error_message_repetitions));
                            return;
                        }
                        else if (!repetitionText.matches(getString(R.string.regex_valid_digit))) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Invalid rep amount " + repetitionText + " entered");
                            repetitionsEditText.setError(getString(R.string.error_message_invalid_digit));
                            return;
                        }
                        int repetitions = Integer.parseInt(repetitionText);
                        String weightText = weightEditText.getText().toString();
                        if (weightText.isEmpty()) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG, "Empty weight entered");
                            weightEditText.setError(getString(R.string.error_message_weight_required));
                            return;
                        }
                        else if (!weightText.matches(getString(R.string.regex_valid_decimal))) {
                            FirebaseCrash.logcat(Log.INFO, LOG_TAG,
                                    "Invalid weight " + weightText + " entered.");
                            weightEditText.setError(getString(R.string.error_message_invalid_weight));
                            return;
                        }
                        double weight =  Double.parseDouble(weightText);
                        mCallback.onCreateExercise(sets, repetitions, description, weight);
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
            mCallback = (CreateExerciseDialogFragmentListener) getArguments().getSerializable(ARG_CALLBACK);
        }
        try {
            if (null == mCallback) {
                mCallback = (CreateExerciseDialogFragmentListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement CreateExerciseDialogFragmentListener");
        }
    }
}
