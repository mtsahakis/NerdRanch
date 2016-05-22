package com.mtsahakis.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    public static final String sPrefSearchQuery = "PREF_SEARCH_QUERY";
    public static final String sPrefLastResultId = "PREF_LAST_RESULT_ID";
    public static final String sPrefCurrentPage = "PREF_CURRENT_PAGE";
    public static final String sPrefIsAlarmOn = "PREF_IS_ALARM_ON";

    public static String getSearchQuery(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(sPrefSearchQuery, null);
    }


    public static void setSearchQuery(Context context, String query) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putString(sPrefSearchQuery, query)
                .apply();
    }

    public static String getLastResultId(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(sPrefLastResultId, null);
    }

    public static void setLastResultId(Context context, String lastResultId) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putString(sPrefLastResultId, lastResultId)
                .apply();
    }

    public static void setAlarmOn(Context context, boolean isAlarmOn) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(sPrefIsAlarmOn, isAlarmOn)
                .apply();
    }

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(sPrefIsAlarmOn, false);
    }
}
