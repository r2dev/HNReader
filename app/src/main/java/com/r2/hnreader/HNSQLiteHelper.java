package com.r2.hnreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * HNSqliteHelper provides basic information about tops and items table, and provide easy access for
 * datasouce class
 */

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

    //item table create statement
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE " + TABLE_ITEM + "(" + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY," +
            COLUMN_ITEM_TYPE + " TEXT," + COLUMN_ITEM_AUTHOR + " TEXT," + COLUMN_ITEM_PARENT + " INTEGER," + COLUMN_ITEM_URL + " TEXT," +
            COLUMN_ITEM_SCORE + " INTEGER," + COLUMN_ITEM_TEXT + " TEXT," + COLUMN_ITEM_TITLE + " TEXT," + COLUMN_ITEM_DESC + " INTEGER);";

    public HNSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(HNSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        onCreate(db);
    }

}
