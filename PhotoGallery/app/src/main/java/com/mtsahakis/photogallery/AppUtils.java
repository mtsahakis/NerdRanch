package com.mtsahakis.photogallery;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AppUtils {

    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;
        View view = activity.getCurrentFocus() == null ? new View(activity) : activity.getCurrentFocus();
        hideKeyboard(activity, view);
    }

    public static void hideKeyboard(Context context, View view) {
        if (context == null) return;
        InputMethodManager inputMethodManager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
