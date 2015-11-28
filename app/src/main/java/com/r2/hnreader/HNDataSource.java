package com.r2.hnreader;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

    public void insertItem(Item item) {
        ContentValues values = new ContentValues();
        System.out.println(item);
        values.put(HNSQLiteHelper.COLUMN_ITEM_TYPE, item.getType());
        values.put(HNSQLiteHelper.COLUMN_ITEM_AUTHOR, item.getBy());
        values.put(HNSQLiteHelper.COLUMN_ITEM_PARENT, item.getParent());
        values.put(HNSQLiteHelper.COLUMN_ITEM_URL, item.getUrl());
        values.put(HNSQLiteHelper.COLUMN_ITEM_SCORE, item.getScore());
        values.put(HNSQLiteHelper.COLUMN_ITEM_TITLE, item.getTitle());
        values.put(HNSQLiteHelper.COLUMN_ITEM_DESC, item.getDescendants());
        sqLiteDatabase.insert(HNSQLiteHelper.TABLE_ITEM, null, values);
    }

    public void updateTopTable() {
        new StoriesJsonTask().execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
    }

    public void getItemFromUrl(String url) {
        new ItemJsonTask().execute(url);
    }

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

    private void freshTopStoriesTable(List<Integer> list) {
        hnsqLiteHelper.onDeleteTopTable(sqLiteDatabase);
        for (int i = 0; i != list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(HNSQLiteHelper.COLUMN_TOP_ID, list.get(i));
            sqLiteDatabase.insert(HNSQLiteHelper.TABLE_TOP, null, values);
        }
    }
    private void freshNewStoriesTable(List<Integer> list) {
        //// TODO: 11/25/2015 new stories table 
    }
    private class ItemJsonTask extends GeneralRequestTask {
        private Item item = null;
        @Override
        protected Object doInBackground(String... params) {
            Object result = super.doInBackground(params);
            Item item = new Item();
            if (result != null) {
                Response response = (Response) result;
                String jsonString = null;
                if (response.code() == 200) {
                    try {
                        jsonString = response.body().string();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                if (jsonString != null) {
                    item = JSON.parseObject(jsonString, Item.class);
                }
            }
            return item;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            item = (Item)o;
            insertItem(item);
        }

    }
    private class StoriesJsonTask extends GeneralRequestTask {
        @Override
        protected Object doInBackground(String... params) {
            Object result = super.doInBackground(params);
            List<Integer> number = new ArrayList<>();
            if (result != null) {
                Response response = (Response) result;
                String jsonString = null;
                if (response.code() == 200) {
                    try {
                        jsonString = response.body().string();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                if (jsonString != null) {
                    number = JSON.parseArray(jsonString, Integer.class);
                }
            }
            return number;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            freshTopStoriesTable((List<Integer>) o);
        }
    }
}
