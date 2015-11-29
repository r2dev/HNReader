package com.r2.hnreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class HNSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_ITEM = "items";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ITEM_TYPE = "type";
    public static final String COLUMN_ITEM_AUTHOR = "user";
    public static final String COLUMN_ITEM_PARENT = "parent_id";
    public static final String COLUMN_ITEM_URL = "url";
    public static final String COLUMN_ITEM_SCORE = "score";
    public static final String COLUMN_ITEM_TITLE = "title";
    public static final String COLUMN_ITEM_DESC = "descendants";
    public static final String COLUMN_ITEM_TEXT = "text";
    public static final String TABLE_TOP = "tops";
    public static final String COLUMN_TOP_ID = "top_id";
    public static final String COLUMN_TOP_ITEM_ID = "item_id";

    //item table create statement
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE " + TABLE_ITEM + "(" + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY," +
            COLUMN_ITEM_TYPE + " TEXT," + COLUMN_ITEM_AUTHOR + " TEXT," + COLUMN_ITEM_PARENT + " INTEGER," + COLUMN_ITEM_URL + " TEXT," +
            COLUMN_ITEM_SCORE + " INTEGER," + COLUMN_ITEM_TEXT + " TEXT," + COLUMN_ITEM_TITLE + " TEXT," + COLUMN_ITEM_DESC + " INTEGER);";
    //top table create statement
    private static final String CREATE_TABLE_TOP = "CREATE TABLE " + TABLE_TOP + "(" + COLUMN_TOP_ID + " INTEGER PRIMARY KEY," +
            COLUMN_TOP_ITEM_ID + " INTEGER);";
    //delete top table statement
    private static final String DELETE_TABLE_TOP = "DELETE FROM " + TABLE_TOP + ";";

    public HNSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_TOP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //// TODO: 11/25/2015 onUpgrade 
    }

    public void onDeleteTopTable(SQLiteDatabase db) {
        db.execSQL(DELETE_TABLE_TOP);
    }
}
