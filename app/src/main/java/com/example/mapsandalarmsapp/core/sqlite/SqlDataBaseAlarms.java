package com.example.mapsandalarmsapp.core.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mapsandalarmsapp.core.helpers.DBNamesHelper;
import com.example.mapsandalarmsapp.feature_alarms.model.AlarmModel;

import java.util.ArrayList;
import java.util.List;

public class SqlDataBaseAlarms {
    private Context context;

    public SqlDataBaseAlarms(Context context) {
        this.context = context;
    }

    public List<AlarmModel> getAllAlarms() {
        List<AlarmModel> list = new ArrayList<>();
        try(
                SQLiteDatabase db = context.openOrCreateDatabase(
                        DBNamesHelper.NAME_DB2, Context.MODE_PRIVATE, null
                );
                Cursor cursor = db.rawQuery(
                        "SELECT *" + " FROM " + DBNamesHelper.TABLE_ALARMS, null
                )
        ) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBNamesHelper.COLUMN_ALARMS_ID));
                    long time =  cursor.getLong(cursor.getColumnIndexOrThrow(DBNamesHelper.COLUMN_ALARMS_TIME));
                    long currentTimeMillis = System.currentTimeMillis();
                    long delayMillis = time - currentTimeMillis;
                    if (delayMillis > 0) {
                        list.add(new AlarmModel(
                                id,
                                time,
                                cursor.getString(cursor.getColumnIndexOrThrow(DBNamesHelper.COLUMN_ALARMS_TITLE)),
                                cursor.getString(cursor.getColumnIndexOrThrow(DBNamesHelper.COLUMN_ALARMS_MESSAGE))
                        ));
                    } else deleteAlarm(id);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertAlarm(AlarmModel alarmModel) {
        try(
                SQLiteDatabase db = context.openOrCreateDatabase(
                        DBNamesHelper.NAME_DB2, Context.MODE_PRIVATE, null
                )
        ) {
            ContentValues values = new ContentValues();
            values.put(DBNamesHelper.COLUMN_ALARMS_ID, alarmModel.getId());
            values.put(DBNamesHelper.COLUMN_ALARMS_TITLE, alarmModel.getTitle());
            values.put(DBNamesHelper.COLUMN_ALARMS_MESSAGE, alarmModel.getMessage());
            values.put(DBNamesHelper.COLUMN_ALARMS_TIME, alarmModel.getAlarmTimeMillis());
            return db.insert(DBNamesHelper.TABLE_ALARMS, null, values) != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateAlarm(int id, long time) {
        try(
                SQLiteDatabase db = context.openOrCreateDatabase(
                        DBNamesHelper.NAME_DB2, Context.MODE_PRIVATE, null
                )
        ) {
            ContentValues values = new ContentValues();
            values.put(DBNamesHelper.COLUMN_ALARMS_TIME, time);
            db.update(
                    DBNamesHelper.TABLE_ALARMS, values,
                    DBNamesHelper.COLUMN_ALARMS_ID + "=" + id, null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAlarm(int id) {
        try(
                SQLiteDatabase db = context.openOrCreateDatabase(
                        DBNamesHelper.NAME_DB2, Context.MODE_PRIVATE, null
                )
        ) {
            db.delete(
                    DBNamesHelper.TABLE_ALARMS,
                    DBNamesHelper.COLUMN_ALARMS_ID + " = " + id,
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
