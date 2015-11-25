package com.r2.hnreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by rren2 on 11/25/2015.
 */
public class HNDataSource {
    private SQLiteDatabase sqLiteDatabase;
    private HNSQLiteHelper hnsqLiteHelper;
    private String[] allColumns = {
            HNSQLiteHelper.COLUMN_ITEM_ID,
            HNSQLiteHelper.COLUMN_ITEM_TYPE,
            HNSQLiteHelper.COLUMN_ITEM_AUTHOR,
            HNSQLiteHelper.COLUMN_ITEM_PARENT,
            HNSQLiteHelper.COLUMN_ITEM_URL,
            HNSQLiteHelper.COLUMN_ITEM_SCORE,
            HNSQLiteHelper.COLUMN_ITEM_TITLE,
            HNSQLiteHelper.COLUMN_ITEM_DESC
    };
    public HNDataSource(Context context) {
        hnsqLiteHelper = new HNSQLiteHelper(context);
    }

    public void openForWriting() throws SQLException {
        sqLiteDatabase = hnsqLiteHelper.getWritableDatabase();
    }

    public void openForReading() throws SQLException {
        sqLiteDatabase = hnsqLiteHelper.getReadableDatabase();
    }

    public void close() {
        hnsqLiteHelper.close();
    }

    public Item createItem(String type, String user, long parent_id, String url, int score, String title, int descendants) {
        ContentValues values = new ContentValues();
        values.put(HNSQLiteHelper.COLUMN_ITEM_TYPE, type);
        values.put(HNSQLiteHelper.COLUMN_ITEM_AUTHOR, user);
        values.put(HNSQLiteHelper.COLUMN_ITEM_PARENT, parent_id);
        values.put(HNSQLiteHelper.COLUMN_ITEM_URL, url);
        values.put(HNSQLiteHelper.COLUMN_ITEM_SCORE, score);
        values.put(HNSQLiteHelper.COLUMN_ITEM_TITLE, title);
        values.put(HNSQLiteHelper.COLUMN_ITEM_DESC, descendants);
        long insertId = sqLiteDatabase.insert(HNSQLiteHelper.TABLE_ITEM, null, values);
        Cursor cursor = sqLiteDatabase.query(HNSQLiteHelper.TABLE_ITEM, allColumns, HNSQLiteHelper.COLUMN_ITEM_ID + " = " +
                insertId, null, null, null, null);
        cursor.moveToFirst();
        Item newItem = cursorToItem(cursor);
        cursor.close();
        return newItem;
    }

    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getLong(0));
        item.setType(cursor.getString(1));
        item.setUser(cursor.getString(2));
        item.setParent_id(cursor.getLong(3));
        item.setUrl(cursor.getString(4));
        item.setScore(cursor.getInt(5));
        item.setTitle(cursor.getString(6));
        item.setDescendants(cursor.getInt(7));
        return item;

    }

    public void freshTops() {

    }

}
