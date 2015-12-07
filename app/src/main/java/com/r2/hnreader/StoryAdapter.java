package com.r2.hnreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * custom story adapter
 */
public class StoryAdapter extends ArrayAdapter<Item>{
    private FloatingActionButton fabAdd, fabShare;
    private HNDataSource dataSource;
    private Activity act;
    public StoryAdapter(Context context, int resource) {
        super(context, resource);
    }
    public StoryAdapter(Context context, int resource, List<Item> objects, FloatingActionButton fab_add, FloatingActionButton fab_share, Activity activity) {
        super(context, resource, objects);
        fabAdd = fab_add;
        fabShare = fab_share;
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
                    System.out.println(p);
                    //http://stackoverflow.com/questions/742171/longclick-event-also-triggers-click-event
                    // return true instead of false to avoid tragging simple click
                    Snackbar.make(v, null, Snackbar.LENGTH_LONG)
                            .setAction("by " + p.getBy() + " " + getTimeAgo(p.getTime()), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(act, UserActivity.class);
                                    intent.putExtra("username", p.getBy());
                                    act.startActivity(intent);
                                }
                            })
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (fabAdd != null) {
                                        fabAdd.hide();
                                    }
                                    fabShare.hide();
                                    super.onDismissed(snackbar, event);
                                }

                                @Override
                                public void onShown(final Snackbar snackbar) {
                                    super.onShown(snackbar);
                                    if (fabAdd != null) {
                                        fabAdd.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dataSource.insertItem(p);
                                                Toast.makeText(act, "Success", Toast.LENGTH_LONG).show();
                                                snackbar.dismiss();
                                                fabAdd.hide();
                                                fabShare.hide();

                                            }
                                        });
                                    }
                                    fabShare.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                            shareIntent.setType("text/plain");
                                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, p.getTitle());
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, p.getUrl());
                                            act.startActivity(Intent.createChooser(shareIntent, "Share Story URL"));
                                            snackbar.dismiss();
                                            if (fabAdd != null) {
                                                fabAdd.hide();
                                            }
                                            fabShare.hide();
                                        }
                                    });
                                    if (fabAdd != null) {
                                        fabAdd.show();
                                    }
                                    fabShare.show();
                                }
                            }).show();
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
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getUrl()));
                            getContext().startActivity(intent);
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
