package com.mtsahakis.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {

    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;


    private AssetManager mAssetManager;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        mAssetManager = context.getApplicationContext().getAssets();
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    public void play(Sound sound) {
        if(sound.getSoundId() == null) {
            return;
        }
        mSoundPool.play(sound.getSoundId(), 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release() {
        mSoundPool.release();
    }

    private void loadSounds() {
        try {
            String[] soundNames = mAssetManager.list(SOUNDS_FOLDER);
            Log.i(TAG, "Found " + soundNames.length + " sounds");
            for(String fileName: soundNames) {
                String assetPath = SOUNDS_FOLDER + "/" + fileName;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not list assets", e);
        }
    }

    private void load(Sound sound) throws IOException {
        AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(assetFileDescriptor, 0);
        sound.setSoundId(soundId);
    }
}
