package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.jdelorenzo.capstoneproject.R;
import com.jdelorenzo.capstoneproject.Utility;
import com.jdelorenzo.capstoneproject.model.Exercise;

import java.io.Serializable;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/*
This fragment is used to create a new exercise.
The CreateExerciseDialogFragmentListener callback can be either passed in on newInstance() or
implemented in the calling activity.
 */
public class EditExerciseDialogFragment extends DialogFragment {
    private static final String ARG_CALLBACK = "callback";
    private static final String ARG_SETS = "sets";
    private static final String ARG_REPS = "reps";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_WEIGHT = "weight";
    private static final String LOG_TAG = EditExerciseDialogFragment.class.getSimpleName();

    @BindView(R.id.exercise) EditText exerciseEditText;
    @BindView(R.id.sets) EditText setsEditText;
    @BindView(R.id.repetitions) EditText repetitionsEditText;
    @BindView(R.id.weight) EditText weightEditText;
    @BindView(R.id.weight_units) TextView weightUnits;

    private Unbinder unbinder;
    public interface EditExerciseDialogFragmentListener extends Serializable {
        void onEditExercise(int reps, int sets, String description, double weight);
    }

    private EditExerciseDialogFragmentListener mCallback;

    public static EditExerciseDialogFragment newInstance(String description, int reps, int sets, double weight,
                                                         EditExerciseDialogFragmentListener callback) {
        Bundle b = new Bundle();
        b.putSerializable(ARG_CALLBACK, callback);
        b.putInt(ARG_SETS, sets);
        b.putInt(ARG_REPS, reps);
        b.putString(ARG_DESCRIPTION, description);
        b.putDouble(ARG_WEIGHT, weight);
        EditExerciseDialogFragment fragment = new EditExerciseDialogFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.dialog_add_exercise, null);
        unbinder = ButterKnife.bind(this, rootView);
        if (args != null) {
            mCallback = (EditExerciseDialogFragmentListener) args.getSerializable(ARG_CALLBACK);
            exerciseEditText.setText(args.getString(ARG_DESCRIPTION, ""));
            repetitionsEditText.setText(String.format(Locale.getDefault(), "%d", args.getInt(ARG_REPS, 0)));
            setsEditText.setText(String.format(Locale.getDefault(), "%d", args.getInt(ARG_SETS, 0)));
            String weightString = Utility.getFormattedWeightStringWithoutUnits(getActivity(), args.getDouble(ARG_WEIGHT));
            weightEditText.setText(weightString);
        }
        weightUnits.setText(Utility.getWeightUnits(getActivity()));
        builder.setTitle(R.string.dialog_edit_exercise_title)
                .setView(rootView)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*
                        Do nothing here because the button is overridden to change the closing behavior.
                         */
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
    public void onStart() {
        super.onStart();
        final AlertDialog d = (AlertDialog)getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    mCallback.onEditExercise(repetitions, sets, description, weight);
                    dismiss();
                }
            });
        }
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
