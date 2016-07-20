package com.stratedgy.dsebuuma.alist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {
    public static String getPreferredSortTerm(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(
                context.getString(R.string.pref_sort_term_key),
                context.getString(R.string.pref_default_sort_term)
        );
    }
}