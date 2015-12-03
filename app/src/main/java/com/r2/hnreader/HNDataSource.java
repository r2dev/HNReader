package com.r2.hnreader;


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
    private static int flagCurrentSetInTopTable = 0;
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
        Item newItem = getItem(insertId);
        return newItem;
    }

    public Item getItem(long i) {
        Cursor cursor = sqLiteDatabase.query(HNSQLiteHelper.TABLE_ITEM, allColumns,
                HNSQLiteHelper.COLUMN_ITEM_ID + " = " + i, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Item item = cursorToItem(cursor);
            cursor.close();
            return item;
        }
        return null;
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
        sqLiteDatabase.insert(HNSQLiteHelper.TABLE_ITEM, null, values);
    }

    public void storeListItem(List<Long> idList) {
        for (int i = 0; i != idList.size(); i++) {
            if (!isInItemTable(idList.get(i))) {
                String url = "https://hacker-news.firebaseio.com/v0/item/" + String.valueOf(idList.get(i)) + ".json?print=pretty";
                storeItemFromUrl(url);
            }
        }
    }

    public List<Item> getListItem(List<Long> idList) {
        List<Item> result = new ArrayList<>();
        for (int i = 0; i != idList.size(); i++) {
            result.add(getItem(idList.get(i)));
        }
        return result;
    }

    public void updateTopTable() {
        new StoriesJsonTask().execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        flagCurrentSetInTopTable = 0;
    }

    public List<Long> getNext10ItemFromTable(String tableName, String tableId) {
        List<Long> result = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(tableName, new String[]{HNSQLiteHelper.COLUMN_TOP_ITEM_ID},
                tableId + ">=" + flagCurrentSetInTopTable + " AND " + tableId + "<=" + flagCurrentSetInTopTable + 10,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                result.add(cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        if (result.size() < 10) {
            flagCurrentSetInTopTable = 0;
        } else {
            flagCurrentSetInTopTable += 10;
        }
        return result;

    }

    public void storeItemFromUrl(String url) {
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

    /**http://stackoverflow.com/a/20416004/1639012
     *
     * @param id
     * @return boolean
     */
    private boolean isInItemTable(Long id) {
        boolean result;
        Cursor cursor = sqLiteDatabase.query(HNSQLiteHelper.TABLE_ITEM, allColumns,
                HNSQLiteHelper.COLUMN_ITEM_ID + " = " + id, null, null, null, null);
        if (cursor.getCount() <= 0) {
            result = false;
        } else {
            result = true;
        }
        cursor.close();
        return result;
    }
    private void freshTopStoriesTable(List<Long> list) {
        hnsqLiteHelper.onDeleteTopTable(sqLiteDatabase);
        for (int i = 0; i != list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(HNSQLiteHelper.COLUMN_TOP_ITEM_ID, list.get(i));
            sqLiteDatabase.insert(HNSQLiteHelper.TABLE_TOP, null, values);
        }
    }
    private void freshNewStoriesTable(List<Long> list) {
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
            System.out.println(item);
            insertItem(item);
        }

    }
    private class StoriesJsonTask extends GeneralRequestTask {
        @Override
        protected Object doInBackground(String... params) {
            Object result = super.doInBackground(params);
            List<Long> number = new ArrayList<>();
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
                    number = JSON.parseArray(jsonString, Long.class);
                }
            }
            return number;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            freshTopStoriesTable((List<Long>) o);
        }
    }
}
