package com.mtsahakis.criminalintent.listview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mtsahakis.criminalintent.model.Crime;
import com.mtsahakis.criminalintent.model.CrimeLab;
import com.mtsahakis.criminalintent.R;

public class CrimeListViewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_crime_listview, container, false);
        final ListView crimeListView = (ListView) view.findViewById(R.id.crime_list);
        final CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        final CrimeAdapter crimeAdapter = new CrimeAdapter(crimeLab.getCrimes(), getActivity());
        crimeListView.setAdapter(crimeAdapter);
        crimeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Crime crime = (Crime) crimeAdapter.getItem(position);
                Toast.makeText(getActivity(), crime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
