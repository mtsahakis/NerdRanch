package com.mtsahakis.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

public class VisibleFragment extends Fragment {

    private static final String TAG = "VisibleFragment";

    private BroadcastReceiver mShowNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Received intent with action: " + intent.getAction());
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mShowNotificationReceiver,
                new IntentFilter(PollService.sActionShowNotification),
                PollService.sPermissionPrivate,
                null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mShowNotificationReceiver);
    }
}
