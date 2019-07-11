package com.studyco.Databases;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Piyush Prakhar on 21-09-2018.
 */
public class AssignmentDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Assignment.db";
    private static final String TABLE_NAME = "Assignment";
    private static final String COL_1 = "assignment_title";
    private static final String COL_2 = "assignment_deadline";
    private static final String COL_3 = "assignment_expected_hours";
    private static final String COL_4 = "assignment_notes";
    private static final String COL_5 = "assignment_completed";
    private static final String COL_6 = "assn_id";
    Context context;

    public AssignmentDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase DB = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( assignment_title String, assignment_deadline String, assignment_expected_hours String,assignment_notes String ,assignment_completed String, assn_id String)");
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
    public boolean update_entry(String col1, String col2, String col3, String col4,String col5)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, col1);
        contentValues.put(COL_2, col2);
        contentValues.put(COL_3, col3);
        contentValues.put(COL_4, col4);
        contentValues.put(COL_5, col5);
        database.update(TABLE_NAME, contentValues,"assignment_title = ? ", new String[] {col1});
        database.close();
        return true;
    }
}
