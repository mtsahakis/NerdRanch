package com.mtsahakis.criminalintent.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;

public class PictureUtils {

    public static Bitmap getScaledBitMap(String path, int inWidth, int inHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if(srcWidth > inWidth || srcHeight > inHeight) {
            if(srcWidth > srcHeight) {
                inSampleSize = Math.round(srcWidth / inWidth);
            } else {
                inSampleSize = Math.round(srcHeight / inHeight);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitMap(String path, Activity activity) {
        Point point = new Point();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        return getScaledBitMap(path, width, height);
    }
}
