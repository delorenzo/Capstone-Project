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

    private Unbinder unbinder;
    public interface CreateExerciseDialogFragmentListener extends Serializable {
        void onCreateExercise(int sets, int reps, String description, int weight);
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
        builder.setTitle(R.string.dialog_create_exercise_title)
                .setIcon(R.drawable.run)
                .setMessage(R.string.dialog_create_exercise_text)
                .setView(rootView)
                .setPositiveButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String description = exerciseEditText.getText().toString();
                        int sets = Integer.parseInt(setsEditText.getText().toString());
                        int repetitions = Integer.parseInt(repetitionsEditText.getText().toString());
                        int weight = Integer.parseInt(weightEditText.getText().toString());
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
