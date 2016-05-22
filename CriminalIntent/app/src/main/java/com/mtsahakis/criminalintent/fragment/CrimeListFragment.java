package com.mtsahakis.criminalintent.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mtsahakis.criminalintent.R;
import com.mtsahakis.criminalintent.activity.CrimePagerActivity;
import com.mtsahakis.criminalintent.model.Crime;
import com.mtsahakis.criminalintent.model.CrimeLab;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private static final String sShowSubtitle = "SHOW_SUBTITLE";

    private CrimeAdapter    mCrimeAdapter;
    private RecyclerView    mCrimeRecyclerView;
    private FrameLayout     mNoCrimesContainer;
    private boolean         mShowSubtitle;

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime>   mCrimes;
        private Context       mContext;

        public CrimeAdapter(List<Crime> crimes, Context context) {
            mCrimes = crimes;
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        public void updateCrimeList(List<Crime> crimes) {
            mCrimes = crimes;
            notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView    mTitle;
        private TextView    mDate;
        private CheckBox    mSolved;
        private Crime       mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.list_item_crime_title);
            mDate = (TextView) itemView.findViewById(R.id.list_item_crime_date);
            mSolved = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onCrimeSelected(mCrime);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitle.setText(mCrime.getTitle());
            mDate.setText(DateFormat.format("EEEE, MMM d, yyyy", mCrime.getDate()));
            mSolved.setChecked(mCrime.isSolved());
        }
    }

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    private Callbacks mCallbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState != null) {
            mShowSubtitle = savedInstanceState.getBoolean(sShowSubtitle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_recyclerview, container, false);
        mNoCrimesContainer = (FrameLayout) view.findViewById(R.id.no_crimes_container);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_list);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCrimeList();
        updateSubtitle();
        displayCrimeContainer();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mShowSubtitle) {
            menuItem.setTitle(R.string.hide_subtitle);
        } else {
            menuItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);
                mCallbacks.onCrimeSelected(crime);
                updateCrimeList();
                displayCrimeContainer();
                return true;
            case R.id.menu_item_show_subtitle:
                mShowSubtitle = !mShowSubtitle;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(sShowSubtitle, mShowSubtitle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void displayCrimeContainer() {
        if(mCrimeAdapter.getItemCount() == 0) {
            mNoCrimesContainer.setVisibility(View.VISIBLE);
        } else {
            mNoCrimesContainer.setVisibility(View.GONE);
        }
    }

    private void updateSubtitle() {
        String subTitle = getString(R.string.app_name);
        if(mShowSubtitle) {
            int crimeCount = mCrimeAdapter.getItemCount();
            subTitle = getResources().getQuantityString(R.plurals.subtitle_format, crimeCount, crimeCount);
        }
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setTitle(subTitle);
    }

    public void updateCrimeList() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes, getActivity());
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.updateCrimeList(crimes);
        }
    }
}
