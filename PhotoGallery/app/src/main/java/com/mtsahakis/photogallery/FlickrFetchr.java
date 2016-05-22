package com.mtsahakis.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    private static final int BUFFER_SIZE = 1024; // 1k byte buffer
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "3d547682d6d173401245861ee8ec5822";
    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest")
            .buildUpon()
            .appendQueryParameter("method", "flickr.photos.getRecent")
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();
    private static final String FETCH_RECENT_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with urlSpec: " + urlSpec);
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytes;
            while ((bytes = in.read(buffer)) > 0) {
                bos.write(buffer, 0, bytes);
            }
            return bos.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public Bitmap getUrlBitmap(String urlSpec) throws IOException {
        byte[] bytes = getUrlBytes(urlSpec);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public List<GalleryItem> fetchRecentPhotos(int currentPage) {
        Uri uri = ENDPOINT
                .buildUpon()
                .appendQueryParameter("method", FETCH_RECENT_METHOD)
                .appendQueryParameter("page", Integer.toString(currentPage))
                .build();
        return fetchItems(uri);
    }

    public List<GalleryItem> searchPhotos(int currentPage, String query) {
        Uri uri = ENDPOINT
                .buildUpon()
                .appendQueryParameter("method", SEARCH_METHOD)
                .appendQueryParameter("page", Integer.toString(currentPage))
                .appendQueryParameter("text", query)
                .build();
        return fetchItems(uri);
    }

    private List<GalleryItem> fetchItems(Uri uri) {
        List<GalleryItem> galleryItems = new ArrayList<>();
        try {
            String result = getUrlString(uri.toString());
            JsonElement jelement = new JsonParser().parse(result);
            JsonObject jobject = jelement.getAsJsonObject();
            jobject = jobject.getAsJsonObject("photos");
            JsonArray photoArray = jobject.getAsJsonArray("photo");
            Gson gson = new Gson();
            for (int i = 0; i < photoArray.size(); i++) {
                JsonObject photo = photoArray.get(i).getAsJsonObject();
                if (!photo.has("url_s")) {
                    continue;
                }
                GalleryItem galleryItem = gson.fromJson(photo, GalleryItem.class);
                galleryItems.add(galleryItem);
            }
        } catch (IOException e) {
            Log.e(TAG, "failed to fetch flickr result", e);
        }
        return galleryItems;
    }
}
