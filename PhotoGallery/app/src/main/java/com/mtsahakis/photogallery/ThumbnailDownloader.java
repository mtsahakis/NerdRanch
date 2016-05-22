package com.mtsahakis.photogallery;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mHandler;
    private Map<T, String> mRequestMap = new ConcurrentHashMap<>();
    private LruCache<String, Bitmap> mLruCache = new LruCache<>(100);

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }

    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> thumbnailDownloadListener) {
        mThumbnailDownloadListener = thumbnailDownloadListener;
    }

    public ThumbnailDownloader() {
        super(TAG);
    }

    public void queueThumbnail(T target, String url) {
        if (url == null || url.isEmpty()) {
            mRequestMap.remove(target);
        } else {
            Log.i(TAG, "Creating Message for URL: " + url);
            mRequestMap.put(target, url);
            mHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg == null || msg.what != MESSAGE_DOWNLOAD) {
                    return;
                }
                T target = (T) msg.obj;
                handleRequest(target);
            }
        };
    }

    private void handleRequest(T target) {
        String url = mRequestMap.get(target);
        if (url == null || url.isEmpty()) {
            return;
        }

        Bitmap bitmap = mLruCache.get(url);
        if (bitmap == null) {
            try {
                Log.i(TAG, "Fetching bitmap for URL: " + url);
                bitmap = new FlickrFetchr().getUrlBitmap(url);
                mLruCache.put(url, bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Failed to download image with URL: " + url, e);
            }
        } else {
            Log.i(TAG, "Image found in cache: " + url);
        }
        if (!mRequestMap.get(target).equals(url)) {
            return;
        }
        mRequestMap.remove(target);
        mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
