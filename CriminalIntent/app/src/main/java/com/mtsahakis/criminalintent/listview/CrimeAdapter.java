package com.mtsahakis.criminalintent.listview;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mtsahakis.criminalintent.model.Crime;
import com.mtsahakis.criminalintent.R;

import java.util.List;

public class CrimeAdapter extends BaseAdapter {

    private final List<Crime> mCrimes;
    private final Context mContext;

    public CrimeAdapter(List<Crime> crimes, Context context) {
        mCrimes = crimes;
        mContext = context;
    }

    @Override
    public Object getItem(int position) {
        return mCrimes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return mCrimes.size();
    }

    private static class ViewHolder {
        public TextView mTitle;
        public TextView mDate;
        public CheckBox mSolved;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_crime, parent, false);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.list_item_crime_title);
            holder.mDate = (TextView) convertView.findViewById(R.id.list_item_crime_date);
            holder.mSolved = (CheckBox) convertView.findViewById(R.id.list_item_crime_solved);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Crime crime = (Crime) getItem(position);
        holder.mTitle.setText(crime.getTitle());
        holder.mDate.setText(DateFormat.format("EEEE, MMM d, yyyy", crime.getDate()));
        holder.mSolved.setChecked(crime.isSolved());

        return convertView;
    }
}
