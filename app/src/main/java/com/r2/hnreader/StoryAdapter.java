package com.r2.hnreader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

        final Item p = getItem(position);
        if (p != null) {
            TextView score = (TextView)v.findViewById(R.id.storyScore);
            TextView title = (TextView)v.findViewById(R.id.storyTitle);
            TextView url = (TextView)v.findViewById(R.id.storyUrl);
            TextView author = (TextView)v.findViewById(R.id.author);

            if (title != null) {
                title.setText(p.getTitle());
            }
            if (url != null) {
                url.setText(Uri.parse(p.getUrl()).getHost());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getUrl()));
                        getContext().startActivity(intent);
                    }
                });
            }
            if (author != null) {
                author.setText(p.getBy());
            }
            if (score != null) {
                score.setText(String.valueOf(p.getScore()));
            }
        }
        return v;
    }
}
