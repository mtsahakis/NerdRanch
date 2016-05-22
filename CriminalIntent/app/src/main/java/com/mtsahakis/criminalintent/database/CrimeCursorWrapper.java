package com.mtsahakis.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.mtsahakis.criminalintent.model.Crime;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuid = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Columns.UUID));
        UUID crimeUUID = UUID.fromString(uuid);
        String crimeTitle = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Columns.TITLE));
        long date = getLong(getColumnIndex(CrimeDBSchema.CrimeTable.Columns.DATE));
        Date crimeDate = new Date();
        crimeDate.setTime(date);
        int solved = getInt(getColumnIndex(CrimeDBSchema.CrimeTable.Columns.SOLVED));
        boolean isCrimeSolved = solved == 1;
        String suspect = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Columns.SUSPECT));
        return new Crime(crimeUUID, crimeTitle, crimeDate, isCrimeSolved, suspect);
    }
}
