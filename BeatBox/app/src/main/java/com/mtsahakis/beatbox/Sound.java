package com.mtsahakis.beatbox;

public class Sound {

    private String  mAssetPath;
    private String  mName;
    private Integer mSoundId;

    public Sound(String assetPath) {
        mAssetPath = assetPath;
        String[] components = mAssetPath.split("/");
        String fileName = components[components.length -1];
        mName = fileName.replace(".wav", "");
    }

    public String getName() {
        return mName;
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }
}
