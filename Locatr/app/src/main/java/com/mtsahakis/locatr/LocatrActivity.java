package com.mtsahakis.locatr;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LocatrActivity extends SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return LocatrFragment.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            googleApiAvailability.getErrorDialog(this, errorCode, REQUEST_ERROR,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.e("LocatrActivity", "google play services not available. finishing.");
                            finish();
                        }
                    }).show();
        }
    }
}
