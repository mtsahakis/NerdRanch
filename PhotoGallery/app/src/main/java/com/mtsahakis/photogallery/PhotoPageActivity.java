package com.mtsahakis.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri uri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof PhotoPageFragment) {
            if (((PhotoPageFragment)fragment).canGoBack()){
                ((PhotoPageFragment)fragment).goBack();
            } else {
                super.onBackPressed();
            }
        }
    }
}
