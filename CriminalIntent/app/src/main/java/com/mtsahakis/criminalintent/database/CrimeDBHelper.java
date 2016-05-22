package com.mtsahakis.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CrimeDBHelper extends SQLiteOpenHelper {

    public static final String sDatabaseName = "crimeBase.db";
    public static final int sVersion = 1;

    public CrimeDBHelper(Context context) {
        super(context, sDatabaseName, null, sVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeDBSchema.CrimeTable.NAME
                        + "("
                        + "_id integer primary key autoincrement, "
                        + CrimeDBSchema.CrimeTable.Columns.UUID + ","
                        + CrimeDBSchema.CrimeTable.Columns.TITLE + ","
                        + CrimeDBSchema.CrimeTable.Columns.DATE + ","
                        + CrimeDBSchema.CrimeTable.Columns.SOLVED + ","
                        + CrimeDBSchema.CrimeTable.Columns.SUSPECT
                        + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
