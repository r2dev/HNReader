package com.r2.hnreader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.BundleCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * custom story adapter
 */
public class StoryAdapter extends ArrayAdapter<Item>{
    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
    private static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
    public static final String KEY_CUSTOM_TABS_MENU_TITLE = "android.support.customtabs.customaction.MENU_ITEM_TITLE";
    public static final String EXTRA_CUSTOM_TABS_MENU_ITEMS = "android.support.customtabs.extra.MENU_ITEMS";
    public static final String KEY_CUSTOM_TABS_PENDING_INTENT = "android.support.customtabs.customaction.PENDING_INTENT";
    private FloatingActionButton fabAdd, fabShare, fabPeople;
    private HNDataSource dataSource;
    private Activity act;

    public StoryAdapter(Context context, int resource) {
        super(context, resource);
    }
    public StoryAdapter(Context context, int resource, List<Item> objects, FloatingActionButton fab_add, FloatingActionButton fab_share, FloatingActionButton fab_people, Activity activity) {
        super(context, resource, objects);
        fabAdd = fab_add;
        fabShare = fab_share;
        fabPeople = fab_people;
        act = activity;
        dataSource = new HNDataSource(activity);
        dataSource.openForWriting();
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
            //when user long press the story
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //http://stackoverflow.com/questions/742171/longclick-event-also-triggers-click-event
                    // return true instead of false to avoid tragging simple click
                    if (fabAdd != null) {
                        fabAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dataSource.insertItem(p);
                                Toast.makeText(act, "Favorite", Toast.LENGTH_LONG).show();
                            }
                        });
                        fabAdd.show();
                    }
                    fabPeople.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(act, UserActivity.class);
                            intent.putExtra("username", p.getBy());
                            act.startActivity(intent);
                        }
                    });
                    fabShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, p.getTitle());
                            shareIntent.putExtra(Intent.EXTRA_TEXT, p.getUrl());
                            act.startActivity(Intent.createChooser(shareIntent, "Share Story URL"));

                        }
                    });

                    fabPeople.show();
                    fabShare.show();
                    return true;
                }
            });
            if (title != null) {
                title.setText(p.getTitle());
            }
            if (url != null) {
                //http://stackoverflow.com/questions/2201917/how-can-i-open-a-url-in-androids-web-browser-from-my-application
                if (p.getUrl() != null) {
                    url.setText(Uri.parse(p.getUrl()).getHost());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle menuItem = new Bundle();
                            ArrayList menuItemBundleList = new ArrayList<>();
                            menuItem.putString(KEY_CUSTOM_TABS_MENU_TITLE, "Share");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getUrl()));
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, p.getTitle());
                            shareIntent.putExtra(Intent.EXTRA_TEXT, p.getUrl());
                            PendingIntent pendingIntent = PendingIntent.getActivity(act.getApplicationContext(), 0,
                                    shareIntent, 0);
                            menuItem.putParcelable(KEY_CUSTOM_TABS_PENDING_INTENT, pendingIntent);
                            menuItemBundleList.add(menuItem);
                            Bundle extras = new Bundle();
                            extras.putBinder(EXTRA_CUSTOM_TABS_SESSION, null);
                            extras.putInt(EXTRA_CUSTOM_TABS_TOOLBAR_COLOR, getContext().getColor(R.color.colorPrimary));
                            extras.putParcelableArrayList(EXTRA_CUSTOM_TABS_MENU_ITEMS, menuItemBundleList);
                            intent.putExtras(extras);
                            act.startActivity(intent);
                        }
                    });
                } else {
                    url.setText("no url provide");
                }
            }
            if (score != null) {
                score.setText(String.valueOf(p.getScore()));
            }
        }
        return v;
    }
    //http://stackoverflow.com/questions/13018550/time-since-ago-library-for-android-java
    //return "10 mins ago" type of string from unix timestamp
    public static String getTimeAgo(long time) {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < 60 * SECOND_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
