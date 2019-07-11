package com.studyco.FirebaseHandling;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Piyush Prakhar on 21-09-2018.
 */
public class FirebaseRegIdDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Firebase.db";
    private static final String TABLE_NAME = "Firebase_reg_id";
    private static final String COL_1 = "reg_id";


    public FirebaseRegIdDatabase(Context context) {

        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase DB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, reg_id STRING )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String col1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, col1);
        long result = db.insert(TABLE_NAME, null, contentValues);
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
}
