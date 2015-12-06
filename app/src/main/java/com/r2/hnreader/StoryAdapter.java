package com.r2.hnreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StoryAdapter extends ArrayAdapter<Item>{
    public StoryAdapter(Context context, int resource) {
        super(context, resource);
    }

    public StoryAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.story_row, null);
        }

        Item p = getItem(position);
        if (p != null) {
            TextView title = (TextView)v.findViewById(R.id.storyTitle);
            TextView url = (TextView)v.findViewById(R.id.storyUrl);
            if (title != null) {
                title.setText(p.getTitle());
            }
            if (url != null) {
                url.setText(p.getUrl());
            }
        }
        return v;
    }
}
