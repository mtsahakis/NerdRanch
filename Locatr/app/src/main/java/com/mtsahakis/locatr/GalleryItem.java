package com.mtsahakis.locatr;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class GalleryItem {

    @SerializedName("id")
    private String mId;

    @SerializedName("url_s")
    private String mURL;

    @SerializedName("title")
    private String mCaption;

    @SerializedName("owner")
    private String mOwner;

    @SerializedName("latitude")
    private double mLat;

    @SerializedName("longitude")
    private double mLon;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String URL) {
        mURL = URL;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public Uri getPhotoPageUri() {
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    @Override
    public String toString() {
        return "GalleryItem{" +
                "mId='" + mId + '\'' +
                ", mURL='" + mURL + '\'' +
                ", mCaption='" + mCaption + '\'' +
                ", mOwner='" + mOwner + '\'' +
                ", mLat=" + mLat +
                ", mLon=" + mLon +
                '}';
    }
}
