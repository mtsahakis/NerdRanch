package com.mtsahakis.criminalintent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.mtsahakis.criminalintent.R;
import com.mtsahakis.criminalintent.fragment.CrimeFragment;
import com.mtsahakis.criminalintent.model.Crime;
import com.mtsahakis.criminalintent.model.CrimeLab;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private List<Crime> mCrimes;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_pager);
        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view);
        mCrimes = CrimeLab.getInstance(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            UUID uuid = (UUID)bundle.getSerializable(EXTRA_CRIME_ID);
            int position = CrimeLab.getInstance(this).getCrimePosition(mCrimes, uuid);
            if(position >= 0) {
                mViewPager.setCurrentItem(position);
            }
        }
    }

    private static final String EXTRA_CRIME_ID = "EXTRA_CRIME_ID";
    public static Intent newIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, uuid);
        return intent;
    }
}
