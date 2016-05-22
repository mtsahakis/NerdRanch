package com.mtsahakis.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

public class SunsetFragment extends Fragment {

    public static final String TAG = "SunsetFragment";

    private int mBlueSky;
    private int mSunsetSky;
    private int mNightSky;

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBlueSky = getResources().getColor(R.color.blue_sky, getActivity().getTheme());
            mSunsetSky = getResources().getColor(R.color.sunset_sky, getActivity().getTheme());
            mNightSky = getResources().getColor(R.color.night_sky, getActivity().getTheme());
        } else {
            mBlueSky = getResources().getColor(R.color.blue_sky);
            mSunsetSky = getResources().getColor(R.color.sunset_sky);
            mNightSky = getResources().getColor(R.color.night_sky);
        }
        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });

        return view;
    }

    private void startAnimation() {
        // height animation -> sun slowly sets bellow sea
        float startY = mSunView.getTop();
        float endY = mSkyView.getBottom();
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", startY, endY)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        // color animation -> sky turn from blue to orange
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mBlueSky, mSunsetSky)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        // color animation -> sky turn from orange to night black
        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSky, mNightSky)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        // start animators
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();
    }
}
