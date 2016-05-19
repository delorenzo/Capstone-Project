package com.jdelorenzo.capstoneproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

public class Utility {
    public static String getFormattedWeightString(Context context, double weight) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = preferences.getString(context.getString(R.string.prefs_weight_unit_key),
                context.getString(R.string.prefs_weight_unit_default));
        if (unit.equals(context.getString(R.string.prefs_weight_unit_imperial_value))) {
            weight = toImperial(weight);
            return String.format(Locale.getDefault(),
                    context.getString(R.string.weight_format_metric), weight);
        }
        else {
            return String.format(Locale.getDefault(),
                    context.getString(R.string.weight_format_imperial), weight);
        }
    }

    public static String getFormattedWeightStringWithoutUnits(Context context, double weight) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = preferences.getString(context.getString(R.string.prefs_weight_unit_key),
                context.getString(R.string.prefs_weight_unit_default));
        if (unit.equals(context.getString(R.string.prefs_weight_unit_imperial_value))) {
            weight = toImperial(weight);
        }
        return String.format(Locale.getDefault(), context.getString(R.string.weight_format), weight);
    }

    public static String getWeightUnits(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = preferences.getString(context.getString(R.string.prefs_weight_unit_key),
                context.getString(R.string.prefs_weight_unit_default));
        if (unit.equals(context.getString(R.string.prefs_weight_unit_imperial_value))) {
            return context.getString(R.string.weight_units_imperial);
        }
        else {
            return context.getString(R.string.weight_units_metric);
        }
    }

    public static double convertWeightToMetric(Context context, double weight) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = preferences.getString(context.getString(R.string.prefs_weight_unit_key),
                context.getString(R.string.prefs_weight_unit_default));
        if (unit.equals(context.getString(R.string.prefs_weight_unit_imperial_value))) {
            return toImperial(weight);
        }
        else {
            return weight;
        }
    }

    private static double toImperial(double kg) {
        return kg * 2.205;
    }

    private static double toMetric (double lb) {
        return lb / 2.205;
    }
}
