package com.studyco.Databases;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Piyush Prakhar on 21-09-2018.
 */
public class SubjectDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Subject.db";
    private static final String TABLE_NAME = "Subject";
    private static final String COL_1 = "subject_title";
    private static final String COL_2 = "subject_rating";
    private static final String COL_3 = "subject_credits";
    private static final String COL_4 = "subject_notes";

    public SubjectDatabase(Context context) {

        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase DB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( subject_title String, subject_rating String, subject_credits String, subject_notes String)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String col1, String col2, String col3 , String col4) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, col1);
        contentValues.put(COL_2, col2);
        contentValues.put(COL_3, col3);
        contentValues.put(COL_4, col4);
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
    public boolean update_entry(String col1, String col2, String col3, String col4)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, col1);
        contentValues.put(COL_2, col2);
        contentValues.put(COL_3, col3);
        contentValues.put(COL_4, col4);
        database.update(TABLE_NAME, contentValues,"subject_title = ? ", new String[] {col1});
        database.close();
        return true;
    }
}
