package com.studyco.Databases;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by Piyush Prakhar on 21-09-2018.
 */
public class LinearTrackerDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LinearTracking.db";
    private static final String TABLE_NAME = "LinearTracker";
    private static final String COL_1 = "subject_name";
    private static final String COL_2 = "start_date";
    private static final String COL_3 = "end_date";
    private static final String COL_4 = "difference";
    Context context;

    public LinearTrackerDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase DB = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( subject_name String, start_date Long, end_date Long,difference Long)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String col1, Long col2, Long col3 , Long col4) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, col1);
        contentValues.put(COL_2, col2);
        contentValues.put(COL_3, col3);
        contentValues.put(COL_4, col4);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public Cursor allData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select  * from " + TABLE_NAME, null);
        return res;
    }

    public void deleteAll() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    public void  deleteData(String index) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_1 + " = ?", new String[]{index});
    }
    public boolean update_entry(String col1, String col2, String col3, Integer col4)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, col1);
        contentValues.put(COL_2, col2);
        contentValues.put(COL_3, col3);
        contentValues.put(COL_4, col4);
        database.update(TABLE_NAME, contentValues,"subject_name = ? ", new String[] {col1});
        database.close();
        return true;
    }
}
