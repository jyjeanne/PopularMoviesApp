package com.jeremy.mycompagny.popularmoviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Utility class
 */
public class Utility {
    public static String getPreferredSortingOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sorting_order_key),
                context.getString(R.string.pref_sorting_order_default_value));
    }
}
