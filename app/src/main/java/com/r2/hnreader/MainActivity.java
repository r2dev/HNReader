package com.r2.hnreader;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HNDataSource dataSource;
    private List<Item> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //test
        dataSource =  new HNDataSource(this);
        dataSource.openForWriting();
        new StoriesJsonTask().execute("https://hacker-news.firebaseio.com/v0/topstories.json");
        new ItemJsonTask().execute("https://hacker-news.firebaseio.com/v0/item/8863.json");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //to do setting page!
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            dataSource.freshTopStoriesTable((List<Long>) o);
        }
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
        }

    }
}
