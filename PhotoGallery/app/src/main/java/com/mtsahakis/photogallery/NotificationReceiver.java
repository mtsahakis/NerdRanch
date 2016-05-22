package com.mtsahakis.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Received intent with action: " + intent.getAction());
        if (getResultCode() != Activity.RESULT_OK) {
            return;
        }
        Log.e(TAG, "Posting notification.");
        Notification notification = intent.getParcelableExtra(PollService.sNotification);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
