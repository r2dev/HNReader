package com.r2.hnreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static List<Item> items = new ArrayList<>();
    private StoryAdapter itemArrayAdapter;
    private static List<Long> idArray = new ArrayList<>();
    private ProgressBar progressBar;
    private boolean internetConnection;
    private ListView listView;
    private static boolean loadingMore = false;
    private FloatingActionButton fabAddButton;
    private FloatingActionButton fabShardButton;
    private FloatingActionButton fabUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        internetConnection = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabAddButton = (FloatingActionButton)findViewById(R.id.fab_add);
        fabShardButton = (FloatingActionButton)findViewById(R.id.fab_share);
        fabUserButton = (FloatingActionButton)findViewById(R.id.fab_people);
        fabAddButton.hide();
        fabShardButton.hide();
        fabUserButton.hide();
        internetConnection = checkInternetConnection();
        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        itemArrayAdapter = new StoryAdapter(this, R.layout.story_row, items,
                fabAddButton, fabShardButton, fabUserButton, MainActivity.this);
        //query list item from top
        if (internetConnection) {
            if (items.size() == 0 && idArray.size() == 0) {
                new StoriesJsonTask().execute("https://hacker-news.firebaseio.com/v0/topstories.json");
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                new ItemsNetworkRequest().execute(items.size());
            }
            listView.setAdapter(itemArrayAdapter);

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Update function
     */
    private void freshTopStories() {
        internetConnection = checkInternetConnection();
        if (internetConnection) {
            items.clear();
            if (listView.getAdapter() == null) {
                listView.setAdapter(itemArrayAdapter);
            }
            itemArrayAdapter.notifyDataSetChanged();
            new StoriesJsonTask().execute("https://hacker-news.firebaseio.com/v0/topstories.json");
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            //to do setting page!
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivity(intent);
//            return true;
//        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_fresh_top) {
            freshTopStories();
            return true;
        }
        if (id == R.id.action_local_data) {
            //go get local data
            Intent intent = new Intent(this, LocalDataActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * check internet connection from system service
     * @return boolean
     */
    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null &&connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Request List of id from story url
     */
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
            idArray = (List<Long>) o;
            //get first 10 items
            new ItemsNetworkRequest().execute(0);
        }
    }
    /**
     * Request 10 items each time and add the items to the global item array
     */
    private class ItemsNetworkRequest extends AsyncTask<Integer, Void, List<Item>> {
        private OkHttpClient client = new OkHttpClient();
        private List<Long> idList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            idList = idArray;
        }

        @Override
        protected List<Item> doInBackground(Integer... params) {
            List<Item> listItem = new ArrayList<>();
            int startPoint = params[0];
            for (int i = startPoint; i != startPoint + 10; i++) {
                Item item;
                Request request = new Request.Builder().url("https://hacker-news.firebaseio.com/v0/item/" +
                        String.valueOf(idList.get(i)) + ".json").addHeader("Accept", "application/json").build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                }catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                if (response != null) {
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
                        listItem.add(item);
                    }
                }
            }
            return listItem;
        }


        @Override
        protected void onPostExecute(List<Item> listItem) {
            loadingMore = true;
            items.addAll(listItem);
            itemArrayAdapter.notifyDataSetChanged();
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            ListView listView = (ListView) findViewById(R.id.listView);
            // next request when user scrolls to the bottom of the list
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {}

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    fabAddButton.hide();
                    fabShardButton.hide();
                    fabUserButton.hide();
                    if (totalItemCount != 0) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        if (lastInScreen == items.size() && (loadingMore)) {
                            loadingMore = false;
                            new ItemsNetworkRequest().execute(lastInScreen);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

}
