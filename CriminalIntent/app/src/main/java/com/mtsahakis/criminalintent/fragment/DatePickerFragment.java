package com.mtsahakis.criminalintent.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.mtsahakis.criminalintent.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String sExtraDate = "EXTRA_DATE";

    private DatePicker mDatePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // inflate date picker widget
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_date_picker);

        // get date from args
        Bundle bundle = getArguments();
        if(bundle != null) {
            Date date = (Date) bundle.getSerializable(ARG_DATE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            mDatePicker.init(year, month, day, null);
        }

        // return dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker_fragment)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult();
                    }
                })
                .create();
    }

    public static DatePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }

    private void sendResult() {
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        Intent data = new Intent();
        data.putExtra(sExtraDate, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
    }
}
