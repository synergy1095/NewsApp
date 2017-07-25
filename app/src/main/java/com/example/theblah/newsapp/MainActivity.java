package com.example.theblah.newsapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.example.theblah.newsapp.Utility.Constants;
import com.example.theblah.newsapp.Utility.DBUtils;
import com.example.theblah.newsapp.Utility.NetworkUtils;
import com.example.theblah.newsapp.Utility.ScheduleUtils;
import com.example.theblah.newsapp.Utility.jsonUtils;
import com.example.theblah.newsapp.models.NewsItem;

import static com.example.theblah.newsapp.Shared.*;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Void>, RecyclerViewAdapter.ItemClickListener {
    static final String TAG = "MainActivity";

    //https://newsapi.org/v1/articles?source=the-next-web&sortBy=latest&apiKey=

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        main = this;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //check shared preferences for previous installation of app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirst = prefs.getBoolean("isfirst", true);

        if (isFirst) { //if not found then do initial loading of db
            refresh();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isfirst", false);
            editor.commit();
        }
        //schedule
        ScheduleUtils.scheduleRefresh(this);
    }

    //on app create/resume etc set up rv to view db
    @Override
    protected void onStart() {
        super.onStart();
        db = new DBUtils(this).getReadableDatabase();
        cursor = DBUtils.getAll(db);
        mAdapter = new RecyclerViewAdapter(cursor, this);
        recyclerView.setAdapter(mAdapter);
        showRV();
    }

    //cleanup for db
    @Override
    protected void onStop() {
        super.onStop();
        db.close();
        cursor.close();
        //cancel all current jobs
        ScheduleUtils.cancelAll(this);
    }

    //refresh button menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //refresh button handler
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemNumber = item.getItemId();

        if (itemNumber == R.id.menu_search) {
            refresh();
        }

        return true;
    }

    //start/restarts the background process to refresh articles triggered by refresh button
    public void refresh() {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(Constants.loaderID, null, this).forceLoad();
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Void>(this) {
            //preload actions
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                loadingRV();

            }

            //background thread does stuff here
            @Override
            public Void loadInBackground() {
                DBUtils.refreshDB(MainActivity.this);
                return null;
            }
        };
    }

    //callback method for background thread finish
    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        showRV();
        //get cursor to updated db
        db = new DBUtils(MainActivity.this).getReadableDatabase();
        cursor = DBUtils.getAll(db);

        //reset adapter to new cursor
        mAdapter = new RecyclerViewAdapter(cursor, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }

    //helper function for opening web page
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //on item click handler for rv items
    @Override
    public void onItemClick(int clickedItemIndex) {
        cursor.moveToPosition(clickedItemIndex);
        String url = cursor.getString(cursor.getColumnIndex(Constants.NewsTable.COLUMN_NAME_URL));
        Log.d(TAG, String.format("Url %s", url));

        openWebPage(url);
    }
}
