package com.mtsahakis.criminalintent.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtsahakis.criminalintent.R;
import com.mtsahakis.criminalintent.model.Crime;
import com.mtsahakis.criminalintent.model.CrimeLab;
import com.mtsahakis.criminalintent.utils.PictureUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private Crime       mCrime;
    private File        mCrimePhotoFile;
    private int         mLastCrimePhotoHeight = 0;

    private TextView    mCrimeTitle;
    private Button      mCrimeDate;
    private Button      mCrimeTime;
    private CheckBox    mCrimeSolved;
    private Button      mChooseSuspect;
    private Button      mSendCrimeReport;
    private Button      mRemoveCrime;
    private Button      mPhoneSuspect;
    private ImageView   mCrimePhoto;
    private ImageButton mCrimeCameraButton;

    private static final String sDialogDate = "DialogDate";
    private static final String sDialogTime = "DialogTime";
    private static final String sDialogPhoto = "DialogPhoto";
    private static final int sRequestCodeDialogDate = 0;
    private static final int sRequestCodeDialogTime = 1;
    private static final int sRequestContact = 2;
    private static final int sRequestPhoto = 3;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
        void onCrimeRemoved(Crime crime);
    }

    private Callbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID uuid = (UUID) getArguments().getSerializable(CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(uuid);
        mCrimePhotoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View toReturn = inflater.inflate(R.layout.fragment_crime, container, false);
        mCrimeTitle         = (TextView) toReturn.findViewById(R.id.crime_title);
        mCrimeDate          = (Button) toReturn.findViewById(R.id.crime_date);
        mCrimeTime          = (Button) toReturn.findViewById(R.id.crime_time);
        mCrimeSolved        = (CheckBox) toReturn.findViewById(R.id.crime_solved);
        mChooseSuspect      = (Button) toReturn.findViewById(R.id.choose_suspect);
        mSendCrimeReport    = (Button) toReturn.findViewById(R.id.send_crime_report);
        mRemoveCrime        = (Button) toReturn.findViewById(R.id.remove_crime);
        mPhoneSuspect       = (Button) toReturn.findViewById(R.id.phone_suspect);
        mCrimePhoto         = (ImageView) toReturn.findViewById(R.id.crime_photo);
        mCrimeCameraButton  = (ImageButton) toReturn.findViewById(R.id.crime_camera);

        mCrimeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());
                datePickerFragment.setTargetFragment(CrimeFragment.this, sRequestCodeDialogDate);
                datePickerFragment.show(fragmentManager, sDialogDate);
            }
        });

        mCrimeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(mCrime.getDate());
                timePickerFragment.setTargetFragment(CrimeFragment.this, sRequestCodeDialogTime);
                timePickerFragment.show(fragmentManager, sDialogTime);
            }
        });

        mCrimeSolved.setChecked(mCrime.isSolved());
        mCrimeSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCrimeTitle.setText(mCrime.getTitle());
        mCrimeSolved.setChecked(mCrime.isSolved());

        mRemoveCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimeLab.getInstance(getActivity()).removeCrime(mCrime);
                mCallbacks.onCrimeRemoved(mCrime);
            }
        });

        mChooseSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getSuspectIntent();
                startActivityForResult(intent, sRequestContact);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(getSuspectIntent(),
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSendCrimeReport.setEnabled(false);
        }

        mSendCrimeReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendCrimeReportIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .setType("text/plain")
                        .setChooserTitle(R.string.send_crime_report_via)
                        .createChooserIntent();
                startActivity(sendCrimeReportIntent);
            }
        });

        mPhoneSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCrime.getSuspect() != null) {
                    String[] contactsQueryFields = new String[]{
                            ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                    };
                    Cursor contactCursor = getActivity().getContentResolver().
                            query(ContactsContract.Contacts.CONTENT_URI,
                                    contactsQueryFields,
                                    ContactsContract.Contacts.DISPLAY_NAME + " = '" + mCrime.getSuspect() + "'",
                                    null, null);
                    try {
                        if (contactCursor.getCount() == 0) {
                            return;
                        }
                        contactCursor.moveToFirst();
                        int _id = contactCursor.getInt(0);
                        int hasPhone = contactCursor.getInt(1);
                        if (hasPhone > 0) {
                            String[] phoneQueryFields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                            Cursor phoneCursor = getActivity().getContentResolver().
                                    query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            phoneQueryFields,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = '" + _id + "'",
                                            null, null);
                            try {
                                if (phoneCursor.getCount() == 0) {
                                    return;
                                }
                                phoneCursor.moveToFirst();
                                String phoneNumber = phoneCursor.getString(0);
                                Uri uri = Uri.parse("tel:" + phoneNumber);
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
                                startActivity(callIntent);
                            } finally {
                                phoneCursor.close();
                            }
                        }
                    } finally {
                        contactCursor.close();
                    }
                }
            }
        });

        if(!canTakePhoto()) {
            mCrimeCameraButton.setEnabled(false);
        }

        mCrimeCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraStartIntent = getCameraIntent();
                Uri uri = Uri.fromFile(mCrimePhotoFile);
                cameraStartIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraStartIntent, sRequestPhoto);
            }
        });

        mCrimePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPhoto();
            }
        });

        ViewTreeObserver viewTreeObserver = mCrimePhoto.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhoto();
            }
        });

        updateDate();
        updateTime();
        updateSuspect();
        updatePhoto();

        return toReturn;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == sRequestCodeDialogDate) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.sExtraDate);
            mCrime.setDate(date);
            updateDate();
            updateCrime();
        } else if(requestCode == sRequestCodeDialogTime) {
            int hour = data.getIntExtra(TimePickerFragment.sExtraHour, -1);
            int minute = data.getIntExtra(TimePickerFragment.sExtraMinute, -1);
            if(hour > 0 && minute > 0) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(mCrime.getDate());
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                mCrime.setDate(calendar.getTime());
                updateTime();
                updateCrime();
            }
        } else if (requestCode == sRequestContact) {
            Uri uri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            Cursor cursor = getActivity().getContentResolver().query(uri,queryFields,null,null,null);
            try {
                if (cursor.getCount() == 0) {
                    return;
                }
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mCrime.setSuspect(suspect);
                updateSuspect();
                updateCrime();
            } finally {
                cursor.close();
            }
        } else if(requestCode == sRequestPhoto) {
            updatePhoto();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void updateCrime() {
        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        mCrimeDate.setText(DateFormat.format("EEEE, MMM d, yyyy", mCrime.getDate()));
    }

    private void updateTime() {
        mCrimeTime.setText(DateFormat.format("HH:mm:ss z", mCrime.getDate()));
    }

    private void updateSuspect() {
        if(mCrime.getSuspect() != null) {
            mChooseSuspect.setText(mCrime.getSuspect());
            mPhoneSuspect.setEnabled(true);
        } else {
            mChooseSuspect.setText(getString(R.string.choose_suspect));
            mPhoneSuspect.setEnabled(false);
        }
    }

    private Intent getSuspectIntent() {
        return new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    }

    private Intent getCameraIntent() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    private boolean canTakePhoto() {
        PackageManager packageManager = getActivity().getPackageManager();
        return (packageManager.resolveActivity(getCameraIntent(),
                PackageManager.MATCH_DEFAULT_ONLY) != null) && (mCrimePhotoFile != null);

    }

    private void updatePhoto() {
        if(mCrimePhotoFile != null && mCrimePhotoFile.exists()) {
            int width = mCrimePhoto.getWidth();
            int height = mCrimePhoto.getHeight();
            if(height != mLastCrimePhotoHeight) {
                mLastCrimePhotoHeight = height;
                Bitmap bitmap = PictureUtils.getScaledBitMap(mCrimePhotoFile.getAbsolutePath(), width, height);
                mCrimePhoto.setImageBitmap(bitmap);
                mCrimePhoto.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        } else {
            mCrimePhoto.setImageBitmap(null);
        }
    }

    private void displayPhoto() {
        if(mCrimePhotoFile != null && mCrimePhotoFile.exists()) {
            PhotoFragment photoFragment = PhotoFragment.newInstance(mCrime.getId());
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            photoFragment.show(fragmentManager, sDialogPhoto);
        }
    }

    private static final String CRIME_ID = "CRIME_ID";
    public static CrimeFragment newInstance(UUID uuid) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CRIME_ID, uuid);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(bundle);
        return crimeFragment;
    }

    private String getCrimeReport() {
        String solved = (mCrime.isSolved()) ? getString(R.string.case_is_solved) : getString(R.string.case_is_not_solved);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd");
        String date = sdf.format(mCrime.getDate());
        String suspect = (mCrime.getSuspect() != null) ?
                getString(R.string.crime_report_suspect, mCrime.getSuspect()) :
                getString(R.string.there_is_no_suspect);
        return getString(R.string.crime_report, mCrime.getTitle(), date, solved, suspect);
    }
}
