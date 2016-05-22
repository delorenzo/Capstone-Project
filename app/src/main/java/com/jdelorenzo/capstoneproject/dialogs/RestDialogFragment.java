package com.jdelorenzo.capstoneproject.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.R;

import java.util.Locale;

/**
 * Extension of {@link DialogFragment} that counts down the user's pre-defined rest period
 * between sets.
 */
public class RestDialogFragment extends DialogFragment {
    CountDownTimer timer;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.dialog_rest, null);
        builder.setTitle(R.string.dialog_rest_title)
                .setView(rootView)
                .setNegativeButton(R.string.action_dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        dismiss();
                    }
                });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final int seconds = preferences.getInt(getString(R.string.prefs_rest_key),
                getResources().getInteger(R.integer.prefs_rest_default));
        final TextView countdownTextView = (TextView) rootView.findViewById(R.id.countdown_text);
        timer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSecondsRemaining = millisUntilFinished / 1000;
                long minutesRemaining = totalSecondsRemaining / 60;
                long secondsRemaining = totalSecondsRemaining % 60;
                countdownTextView.setText(String.format(Locale.getDefault(),
                        getString(R.string.format_rest),
                        minutesRemaining,
                        secondsRemaining));
            }

            @Override
            public void onFinish() {
                countdownTextView.setText(getString(R.string.dialog_rest_finished));
            }
        }.start();
        return builder.create();
    }
}
