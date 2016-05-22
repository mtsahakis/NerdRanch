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
import android.widget.TimePicker;

import com.mtsahakis.criminalintent.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {

    public static final String sExtraHour = "EXTRA_HOUR";
    public static final String sExtraMinute = "EXTRA_MINUTE";
    private static final String ARG_DATE = "date";
    private TimePicker mTimePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_time_picker);

        Bundle bundle = getArguments();
        if(bundle != null) {
            Date date = (Date) bundle.getSerializable(ARG_DATE);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult();
                    }
                })
                .create();
    }

    public static TimePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment;
    }

    private void sendResult() {
        int hour = mTimePicker.getCurrentHour();
        int minute = mTimePicker.getCurrentMinute();
        Intent intent = new Intent();
        intent.putExtra(sExtraHour, hour);
        intent.putExtra(sExtraMinute, minute);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
