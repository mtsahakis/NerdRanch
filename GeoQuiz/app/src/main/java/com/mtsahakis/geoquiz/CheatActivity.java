package com.mtsahakis.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.mtsahakis.geoquiz.extra_answer_is_true";

    private TextView    mAnswerText;
    private Button      mShowAnswerButton;
    private boolean     mIsAnswerTrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mIsAnswerTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAnswerText = (TextView) findViewById(R.id.answerText);
        mShowAnswerButton = (Button) findViewById(R.id.showAnswerButton);
        TextView versionText = (TextView) findViewById(R.id.versionText);

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK);

                if(mIsAnswerTrue) {
                    mAnswerText.setText(R.string.true_button);
                } else {
                    mAnswerText.setText(R.string.false_button);
                }

                startAnimation();
            }
        });

        versionText.setText(getString(R.string.api_level) + " " + Build.VERSION.SDK_INT);
    }

    public static Intent newIntent(Context context, boolean isAnswerTrue) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE, isAnswerTrue);
        return intent;
    }

    private void startAnimation() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = mShowAnswerButton.getWidth() / 2;
            int cy = mShowAnswerButton.getHeight() / 2;
            float radius = mShowAnswerButton.getWidth();
            Animator animator = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            });
            animator.start();
        } else {
            mShowAnswerButton.setVisibility(View.INVISIBLE);
        }
    }
}
