package com.r2.hnreader;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.List;

/**
 * activity class for viewing the local database
 */
public class LocalDataActivity extends AppCompatActivity {
    private HNDataSource dataSource;
    private List<Item> itemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        FloatingActionButton fabShare = (FloatingActionButton) findViewById(R.id.fab_share_2);
        ListView listView = (ListView) findViewById(R.id.listView2);
        fabShare.hide();
        dataSource = new HNDataSource(this);
        dataSource.openForReading();
        itemList = dataSource.getItems();
        ArrayAdapter<Item> itemArrayAdapter = new StoryAdapter(this, R.layout.story_row, itemList, null, fabShare, LocalDataActivity.this);
        listView.setAdapter(itemArrayAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
