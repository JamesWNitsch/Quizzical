package com.jamesn.quizzical;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by James on 1/4/2016.
 */
public class API_Getter extends AsyncTask<String, String, String> {
    OkHttpClient client = new OkHttpClient();

    // Retrieves data from the internet using OkHttp
    @Override
    protected String doInBackground(String... params) {
        Request request = new Request.Builder().url(params[0]).build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String responseBody = null;

        try {
            responseBody=response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    // Calls back to the DataHolder to parse the information we pulled down
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            MainActivity.dataHolder.parseJSON(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

