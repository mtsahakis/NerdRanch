package com.mtsahakis.criminalintent.activity;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mtsahakis.criminalintent.R;
import com.mtsahakis.criminalintent.fragment.CrimeFragment;
import com.mtsahakis.criminalintent.fragment.CrimeListFragment;
import com.mtsahakis.criminalintent.model.Crime;
import com.mtsahakis.criminalintent.model.CrimeLab;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    private CrimeFragment mCrimeFragment;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mCrimeFragment = CrimeFragment.newInstance(crime.getId());
                    fragmentManager.beginTransaction()
                            .replace(R.id.detail_fragment_container, mCrimeFragment)
                            .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        updateCrimeList();
    }

    @Override
    public void onCrimeRemoved(Crime crime) {
        updateCrimeList();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .remove(mCrimeFragment)
                .commit();
    }

    private void updateCrimeList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CrimeListFragment crimeListFragment = (CrimeListFragment)fragmentManager.findFragmentById(R.id.fragment_container);
        crimeListFragment.updateCrimeList();
    }
}
