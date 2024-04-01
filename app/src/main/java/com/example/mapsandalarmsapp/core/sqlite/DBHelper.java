package com.example.mapsandalarmsapp.core.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mapsandalarmsapp.core.helpers.DBNamesHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d(TAG, "Proceso 'DB Garantias', DEBUG: VERSION DE LA BASE DE DATOS: " + version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBNamesHelper.TABLE_ALARMS + "(" +
                DBNamesHelper.COLUMN_ALARMS_TITLE + " TEXT, " +
                DBNamesHelper.COLUMN_ALARMS_MESSAGE + " TEXT, " +
                DBNamesHelper.COLUMN_ALARMS_TIME + " BIGINT, " +
                DBNamesHelper.COLUMN_ALARMS_ID + " INTEGER, " +
                " primary key(" + DBNamesHelper.COLUMN_ALARMS_ID + " ));"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {

        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            super.onDowngrade(db, oldVersion, newVersion);

        } catch (Exception e) {

        }
    }

}