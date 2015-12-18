package com.r2.hnreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

/**
 * user activity when receive the username from mainactivity then request the information
 */
public class UserActivity extends AppCompatActivity {
    private TextView nameField;
    private TextView timeField;
    private TextView aboutField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        nameField = (TextView)findViewById(R.id.nameField);
        timeField = (TextView)findViewById(R.id.timeField);
        aboutField = (TextView)findViewById(R.id.aboutField);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String user = intent.getStringExtra("username");
        String url = "https://hacker-news.firebaseio.com/v0/user/" + user + ".json";
        new UserJsonTask().execute(url);
    }
    /**
     * Thread task to request one user from user
     */
    private class UserJsonTask extends GeneralRequestTask {
        @Override
        protected Object doInBackground(String... params) {
            Object result = super.doInBackground(params);
            User u = new User();
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
                    JSONObject object = JSON.parseObject(jsonString);
                    u.setId(object.getString("id"));
                    u.setAbout(object.getString("about"));
                    u.setCreated(object.getLong("created"));
                }
            }
            return u;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            User u = (User)o;
            nameField.setText(u.getId());
            timeField.setText(StoryAdapter.getTimeAgo(u.getCreated()));
            aboutField.setText(u.getAbout());
        }
    }

    /**
     * User class for parsing
     */
    private class User {
        private String about = "";
        private long created;
        private long delay;
        private String id = "";
        private long karma;

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public long getCreated() {
            return created;
        }

        public void setCreated(long created) {
            this.created = created;
        }

        public long getDelay() {
            return delay;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getKarma() {
            return karma;
        }

        public void setKarma(long karma) {
            this.karma = karma;
        }


        @Override
        public String toString() {
            return "User{" +
                    "about='" + about + '\'' +
                    ", created=" + created +
                    ", delay=" + delay +
                    ", id='" + id + '\'' +
                    ", karma=" + karma +
                    '}';
        }
    }

}
