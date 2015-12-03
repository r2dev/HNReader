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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HNDataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //test
        dataSource =  new HNDataSource(this);
        dataSource.openForWriting();
        dataSource.updateTopTable();
        ListView lv = (ListView)findViewById(R.id.listView);
        List<Long> longList = dataSource.getNext10ItemFromTable(HNSQLiteHelper.TABLE_TOP, HNSQLiteHelper.COLUMN_TOP_ID);
        dataSource.storeListItem(longList);
        List<Item> a = dataSource.getListItem(longList);
        System.out.println(a);
        ArrayAdapter<Item> itemArrayAdapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, a);
        lv.setAdapter(itemArrayAdapter);
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
}
