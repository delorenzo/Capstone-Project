package com.jdelorenzo.capstoneproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

public class Utility {
    public static String getFormattedWeightString(Context context, long weight) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = preferences.getString(context.getString(R.string.prefs_weight_unit_key),
                context.getString(R.string.prefs_weight_unit_default));
        if (unit.equals(context.getString(R.string.prefs_weight_unit_imperial_value))) {
            return String.format(Locale.getDefault(),
                    context.getString(R.string.weight_format_metric), weight);
        }
        else {
            return String.format(Locale.getDefault(),
                    context.getString(R.string.weight_format_imperial), weight);
        }
    }
}
