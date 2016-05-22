package com.mtsahakis.criminalintent.model;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID uuid, String title, Date date, boolean isSolved, String suspect) {
        mId = uuid;
        mTitle = title;
        mDate = date;
        mSolved = isSolved;
        mSuspect = suspect;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFileName() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Crime)) {
            return false;
        }
        if(o == this) {
            return true;
        }
        return ((Crime)o).getId().equals(getId());
    }
}
