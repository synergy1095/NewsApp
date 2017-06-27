package com.example.theblah.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.theblah.newsapp.models.NewsItem;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";

    //https://newsapi.org/v1/articles?source=the-next-web&sortBy=latest&apiKey=
    public static final String SOURCE = "the-next-web";
    public static final String SORTBY = "latest";

    private ProgressBar progress;
    private RecyclerView recyclerView;
    private TextView errorView;
    private RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        errorView = (TextView) findViewById(R.id.tv_error_message_display);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    class NetworkTask extends AsyncTask<URL, Void, ArrayList<NewsItem>> {
        String SOURCE, SORT_BY;

        NetworkTask(String source, String sortBy) {
            SOURCE = source;
            SORT_BY = sortBy;
        }

        private void showError() {
            errorView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        private void showRV() {
            errorView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<NewsItem> doInBackground(URL... params) {
            ArrayList<NewsItem> result = null;
            URL url = NetworkUtils.makeURL(SOURCE, SORT_BY);
            Log.d(TAG, "url: " + url.toString());
            try {
                String json = NetworkUtils.getResponseFromHttpUrl(url);
                result = jsonUtils.parseJSON(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsItem> s) {
            super.onPostExecute(s);
            progress.setVisibility(View.GONE);
            if (s != null) {
                mAdapter = new RecyclerViewAdapter(s, new RecyclerViewAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int clickedItemIndex) {
                        if (mAdapter.getItemCount() != 0) {
                            String url = mAdapter.getData().get(clickedItemIndex).getUrl();
                            Log.d(TAG, String.format("Url %s", url));
                            openWebPage(url);
                        }
                    }
                });
                recyclerView.setAdapter(mAdapter);
                showRV();
            } else {
                showError();
            }
        }

        public void openWebPage(String url) {
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}
