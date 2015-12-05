package com.r2.hnreader;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HNDataSource dataSource;
    private List<Item> items = new ArrayList<>();
    private ArrayAdapter<Item> itemArrayAdapter;
    private List<Long> idArray = new ArrayList<>();
    private int flagReadingTop = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //test
        dataSource = new HNDataSource(this);
        dataSource.openForWriting();
        //fresh and store top table data
        new StoriesJsonTask().execute("https://hacker-news.firebaseio.com/v0/topstories.json");
        ListView listView = (ListView) findViewById(R.id.listView);

        itemArrayAdapter = new ArrayAdapter<Item>(this,
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemArrayAdapter);

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
            idArray = (List<Long>) o;
            System.out.println(idArray);
            new ItemsNetworkRequest().execute(flagReadingTop);
        }
    }
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
                Item item = null;
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
                        System.out.println(item);
                        listItem.add(item);
                        dataSource.insertItem(item);
                    }
                }
            }
            return listItem;
        }


        @Override
        protected void onPostExecute(List<Item> listItem) {
            items.addAll(listItem);
            itemArrayAdapter.notifyDataSetChanged();
            //add more button
            Button button = (Button)findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ItemsNetworkRequest().execute(flagReadingTop+10);
                }
            });

        }
    }

}
