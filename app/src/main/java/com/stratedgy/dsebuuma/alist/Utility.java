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

    public static boolean isInFavoriteMode(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(
                context.getString(R.string.pref_sort_term_key),
                context.getString(R.string.pref_default_sort_term)
        ).equals(context.getString(R.string.pref_favorite_sort_term));
    }

    public static String formatRuntime(Context context, int runtime) {
        return context.getString(R.string.runtime_format, runtime);
    }

    public static String formatRating(Context context, float rating) {
        return context.getString(R.string.rating_format, rating);
    }

    public static String getYearFromDateString(String dateString) {
        return dateString.split("-")[0];
    }
}
