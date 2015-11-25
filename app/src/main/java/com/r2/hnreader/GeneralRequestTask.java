package com.r2.hnreader;
import android.os.AsyncTask;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import java.io.IOException;

public class GeneralRequestTask extends AsyncTask<String, Void, Object> {
    private OkHttpClient client = new OkHttpClient();
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(String... params) {
        String url = params[0];
        Request request = new Request.Builder().url(url).
                addHeader("Accept", "application/json").build();
        Object response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
