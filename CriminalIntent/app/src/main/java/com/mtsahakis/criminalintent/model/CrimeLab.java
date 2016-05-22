package com.mtsahakis.criminalintent.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.mtsahakis.criminalintent.database.CrimeCursorWrapper;
import com.mtsahakis.criminalintent.database.CrimeDBHelper;
import com.mtsahakis.criminalintent.database.CrimeDBSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mSQLiteDatabase = new CrimeDBHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab getInstance(Context context) {
        if(sCrimeLab == null) {
            synchronized (CrimeLab.class) {
                if(sCrimeLab == null) {
                    sCrimeLab = new CrimeLab(context);
                }
            }
        }
        return sCrimeLab;
    }

    /*** GET Crimes *************************************/
    public Crime getCrime(UUID uuid) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeDBSchema.CrimeTable.Columns.UUID + " = ?",
                new String[]{uuid.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null,null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Crime crime = cursor.getCrime();
                crimes.add(crime);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public int getCrimePosition(List<Crime> crimes, UUID uuid) {
        for (int i = 0; i < crimes.size(); i++) {
            Crime crime = crimes.get(i);
            if(crime.getId().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

    /*** ADD UPDATE DELETE Crimes **************************/
    public void addCrime(Crime crime) {
        ContentValues contentValues = getContentValues(crime);
        mSQLiteDatabase.insert(CrimeDBSchema.CrimeTable.NAME, null, contentValues);
    }

    public void updateCrime(Crime crime) {
        ContentValues contentValues = getContentValues(crime);
        mSQLiteDatabase.update(CrimeDBSchema.CrimeTable.NAME,
                contentValues, CrimeDBSchema.CrimeTable.Columns.UUID + " = ?",
                new String[]{crime.getId().toString()});
    }

    public void removeCrime(Crime crime) {
        mSQLiteDatabase.delete(CrimeDBSchema.CrimeTable.NAME,
                CrimeDBSchema.CrimeTable.Columns.UUID + " = ?",
                new String[]{crime.getId().toString()});
    }

    /*** private utility method ***************************/
    private ContentValues getContentValues(Crime crime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeDBSchema.CrimeTable.Columns.UUID, crime.getId().toString());
        contentValues.put(CrimeDBSchema.CrimeTable.Columns.TITLE, crime.getTitle());
        contentValues.put(CrimeDBSchema.CrimeTable.Columns.DATE, crime.getDate().getTime());
        contentValues.put(CrimeDBSchema.CrimeTable.Columns.SOLVED, crime.isSolved() ? 1 : 0);
        contentValues.put(CrimeDBSchema.CrimeTable.Columns.SUSPECT, crime.getSuspect());
        return contentValues;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mSQLiteDatabase.query(
                CrimeDBSchema.CrimeTable.NAME,
                null,   // return all columns
                whereClause,
                whereArgs,
                null,   // groupBY
                null,   // having
                "_id"    // orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }

    /*** Photo File ****************************************/
    public File getPhotoFile(Crime crime) {
        File photoDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(photoDir == null) {
            return null;
        }
        return new File(photoDir, crime.getPhotoFileName());
    }
}
