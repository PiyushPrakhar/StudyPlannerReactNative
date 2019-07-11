package com.studyco.Databases;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Piyush Prakhar on 21-09-2018.
 */
public class ProjectDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Project.db";
    private static final String TABLE_NAME = "Project";
    private static final String COL_1 = "project_title";
    private static final String COL_2 = "project_deadline";
    private static final String COL_3 = "project_expected_hours";
    private static final String COL_4 = "project_notes";
    private static final String COL_5 = "project_completed";
    private static final String COL_6 = "project_id";

    public ProjectDatabase(Context context) {

        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase DB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( project_title String, project_deadline String, project_expected_hours String,project_notes String,project_completed String, project_id String)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String col1, String col2, String col3 , String col4, String col5, String col6) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, col1);
        contentValues.put(COL_2, col2);
        contentValues.put(COL_3, col3);
        contentValues.put(COL_4, col4);
        contentValues.put(COL_5, col5);
        contentValues.put(COL_6, col6);

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
    public boolean update_entry(String col1, String col2, String col3, String col4,String col5)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, col1);
        contentValues.put(COL_2, col2);
        contentValues.put(COL_3, col3);
        contentValues.put(COL_4, col4);
        contentValues.put(COL_5, col5);
        database.update(TABLE_NAME, contentValues,"project_title = ? ", new String[] {col1});
        database.close();
        return true;
    }
}
