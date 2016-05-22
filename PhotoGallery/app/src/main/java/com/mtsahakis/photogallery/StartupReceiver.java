package com.mtsahakis.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Received intent with action: " + intent.getAction());
        boolean isAlarmOn = QueryPreferences.isAlarmOn(context);
        PollService.setAlarmService(context, isAlarmOn);
    }
}
