package com.mtsahakis.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String BUNDLE_KEY_INDEX = "index";
    private static final String BUNDLE_KEY_CHEAT_MAP = "cheatMap";
    public static final int REQUEST_CODE_CHEAT = 1;

    private HashMap<Integer, Boolean> mCheatMap = new HashMap<>();
    private TextView mQuestionText;
    private int mCurrentIndex;
    private final Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_america, true),
            new Question(R.string.question_asia, true)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_quiz);

        mQuestionText = (TextView) findViewById(R.id.questionText);
        Button mTrueButton = (Button) findViewById(R.id.trueButton);
        Button mFalseButton = (Button) findViewById(R.id.falseButton);
        Button mCheatButton = (Button) findViewById(R.id.cheatButton);
        ImageButton mNextButton = (ImageButton) findViewById(R.id.nextButton);
        ImageButton mPreviousButton = (ImageButton) findViewById(R.id.previousButton);

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CheatActivity.newIntent(QuizActivity.this, mQuestionBank[mCurrentIndex].isAnswerTrue());
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1 ) % mQuestionBank.length;
                updateAnswerText();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex == 0) return;
                mCurrentIndex -= 1;
                updateAnswerText();
            }
        });

        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(BUNDLE_KEY_INDEX);
            mCheatMap = (HashMap<Integer, Boolean>)savedInstanceState.getSerializable(BUNDLE_KEY_CHEAT_MAP);
        }

        updateAnswerText();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
        outState.putInt(BUNDLE_KEY_INDEX, mCurrentIndex);
        outState.putSerializable(BUNDLE_KEY_CHEAT_MAP, mCheatMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CHEAT) {
            if(resultCode == Activity.RESULT_OK) {
                mCheatMap.put(mCurrentIndex, true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /******************************** Private state changing methods ******************************/
    private void checkAnswer(boolean clickedTrue) {
        int messageID;
        if(mCheatMap.get(mCurrentIndex) != null && mCheatMap.get(mCurrentIndex)) {
            messageID = R.string.you_cheated;
        } else {
            boolean isAnswerTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            messageID = (isAnswerTrue == clickedTrue) ? R.string.correct_toast : R.string.incorrect_toast;
        }

        Toast.makeText(QuizActivity.this, messageID, Toast.LENGTH_SHORT).show();
    }

    private void updateAnswerText() {
        Question question = mQuestionBank[mCurrentIndex];
        mQuestionText.setText(question.getTextResID());
    }
}
