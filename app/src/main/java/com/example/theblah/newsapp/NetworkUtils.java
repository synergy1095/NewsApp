package com.example.theblah.newsapp;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by TheBlah on 6/20/2017.
 */


public class NetworkUtils {
    public static final String TAG = "NetworkUtils";

    //https://newsapi.org/v1/articles?source=the-next-web&sortBy=latest&apiKey=
    public static final String BASE_URL = "https://newsapi.org/v1/articles";
    public static final String PARAM_SOURCE = "source";
    public static final String PARAM_SORT_BY = "sortBy";
    public static final String PARAM_API_KEY = "apiKey";

    public static URL makeURL(String source, String sortBy) {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_SOURCE, source)
                .appendQueryParameter(PARAM_SORT_BY, sortBy)
                .appendQueryParameter(PARAM_API_KEY, Credentials.apiKey).build();

        URL url = null;
        try {
            String urlString = uri.toString();
            Log.d(TAG, "URL: " + urlString);
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            return (scanner.hasNext()) ? scanner.next() : null;
        } finally {
            urlConnection.disconnect();
        }
    }
}
