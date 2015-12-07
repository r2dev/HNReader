package com.r2.hnreader;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provide basic database utilities including create database, get writing and reading
 * permission, close database connection, create item, Read items from item table,
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
        Item item = null;
        values.put(HNSQLiteHelper.COLUMN_ITEM_TYPE, type);
        values.put(HNSQLiteHelper.COLUMN_ITEM_AUTHOR, user);
        values.put(HNSQLiteHelper.COLUMN_ITEM_PARENT, parent_id);
        values.put(HNSQLiteHelper.COLUMN_ITEM_URL, url);
        values.put(HNSQLiteHelper.COLUMN_ITEM_SCORE, score);
        values.put(HNSQLiteHelper.COLUMN_ITEM_TITLE, title);
        values.put(HNSQLiteHelper.COLUMN_ITEM_DESC, descendants);
        long insertId = sqLiteDatabase.insert(HNSQLiteHelper.TABLE_ITEM, null, values);
        Cursor cursor = sqLiteDatabase.query(HNSQLiteHelper.TABLE_ITEM, allColumns,
                HNSQLiteHelper.COLUMN_ITEM_ID + " = " + insertId, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            item = cursorToItem(cursor);
            cursor.close();
        }
        return item;
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(HNSQLiteHelper.TABLE_ITEM,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }
    public void insertItem(Item item) {
        ContentValues values = new ContentValues();
        values.put(HNSQLiteHelper.COLUMN_ITEM_ID, item.getId());
        values.put(HNSQLiteHelper.COLUMN_ITEM_TYPE, item.getType());
        values.put(HNSQLiteHelper.COLUMN_ITEM_AUTHOR, item.getBy());
        values.put(HNSQLiteHelper.COLUMN_ITEM_PARENT, item.getParent());
        values.put(HNSQLiteHelper.COLUMN_ITEM_URL, item.getUrl());
        values.put(HNSQLiteHelper.COLUMN_ITEM_SCORE, item.getScore());
        values.put(HNSQLiteHelper.COLUMN_ITEM_TITLE, item.getTitle());
        values.put(HNSQLiteHelper.COLUMN_ITEM_DESC, item.getDescendants());
        //avoid sql exception from duplicate insert
        sqLiteDatabase.insertWithOnConflict(HNSQLiteHelper.TABLE_ITEM, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    // convert from cursor to item instance
    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getLong(0));
        item.setType(cursor.getString(1));
        item.setBy(cursor.getString(2));
        item.setParent(cursor.getLong(3));
        item.setUrl(cursor.getString(4));
        item.setScore(cursor.getInt(5));
        item.setTitle(cursor.getString(6));
        item.setDescendants(cursor.getInt(7));
        return item;
    }

    public void freshTopStoriesTable(List<Long> list) {
        hnsqLiteHelper.onDeleteTopTable(sqLiteDatabase);
        for (int i = 0; i != list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(HNSQLiteHelper.COLUMN_TOP_ITEM_ID, list.get(i));
            sqLiteDatabase.insert(HNSQLiteHelper.TABLE_TOP, null, values);
        }
    }


}
