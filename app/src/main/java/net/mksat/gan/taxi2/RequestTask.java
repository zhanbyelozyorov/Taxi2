package net.mksat.gan.taxi2;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ByelozyorovZ on 05.11.2016.
 */

public class RequestTask extends AsyncTask<String, Void, String> {
    private MainActivity m_parent = null;
    private Exception m_error = null;

    public RequestTask(MainActivity parent) {
        this.m_parent = parent;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection;

        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream answerIS = urlConnection.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(answerIS));
            return r.readLine();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            m_error = e;
        } catch (IOException e) {
            e.printStackTrace();
            m_error = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (m_error != null) {
            m_error.printStackTrace();
            return;
        }
        try {
            m_parent.fillList(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
