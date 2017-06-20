package com.example.theblah.newsapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";

    //https://newsapi.org/v1/articles?source=the-next-web&sortBy=latest&apiKey=
    public static final String SOURCE = "the-next-web";
    public static final String SORTBY = "latest";

    private ProgressBar progress;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.displayJSON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemNumber = item.getItemId();

        if (itemNumber == R.id.menu_search) {
            NetworkTask task = new NetworkTask(SOURCE, SORTBY);
            task.execute();
        }
        return true;
    }

    class NetworkTask extends AsyncTask<URL, Void, String> {
        String SOURCE, SORT_BY;

        NetworkTask(String source, String sortBy){
            SOURCE = source;
            SORT_BY = sortBy;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            String result = null;
            URL url = NetworkUtils.makeURL(SOURCE, SORT_BY);
            Log.d(TAG, "url: " + url.toString());
            try {
                result = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.setVisibility(View.GONE);
            if (s == null) {
                textView.setText("Sorry, no text was received.");
            } else {
                textView.setText(s);
            }
        }
    }
}
