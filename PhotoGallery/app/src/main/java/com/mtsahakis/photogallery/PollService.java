package com.mtsahakis.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final long sPollInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public static final String sActionShowNotification = "com.mtsahakis.photogallery.ACTION_SHOW_NOTIFICATION";
    public static final String sPermissionPrivate = "com.mtsahakis.photogallery.PRIVATE";
    public static final String sNotification = "NOTIFICATION";

    public static void setAlarmService(Context context, boolean isAlarmOn) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isAlarmOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), sPollInterval, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        QueryPreferences.setAlarmOn(context, isAlarmOn);
    }

    public static boolean isAlarmOn(Context context) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!AppUtils.isNetworkAvailableAndConnected(this)) {
            return;
        }

        Log.i(TAG, "received an intent: " + intent);

        String lastResultId = QueryPreferences.getLastResultId(this);
        String searchQuery = QueryPreferences.getSearchQuery(this);

        List<GalleryItem> items;
        if (searchQuery == null) {
            items = new FlickrFetchr().fetchRecentPhotos(0);
        } else {
            items = new FlickrFetchr().searchPhotos(0, searchQuery);
        }

        if (items == null || items.size() == 0) {
            return;
        }

        String lastFetchResultId = items.get(0).getId();
        if (lastFetchResultId.equals(lastResultId)) {
            Log.i(TAG, "got an old result back");
        } else {
            Log.i(TAG, "got a new result");
            QueryPreferences.setLastResultId(this, lastFetchResultId);
            PendingIntent pi = PendingIntent.getActivity(this, 0, PhotoGalleryActivity.newIntent(this), 0);
            Notification notification = new NotificationCompat
                    .Builder(this)
                    .setTicker(getString(R.string.new_pictures_ticker))
                    .setContentTitle(getString(R.string.new_pictures_title))
                    .setContentText(getString(R.string.new_pictures_text))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .build();
            showBackgroundNotification(notification);
        }
    }

    private void showBackgroundNotification(Notification notification) {
        Intent intent = new Intent(sActionShowNotification);
        intent.putExtra(sNotification, notification);
        sendOrderedBroadcast(intent, sPermissionPrivate, null, null, Activity.RESULT_OK, null, null);
    }
}
