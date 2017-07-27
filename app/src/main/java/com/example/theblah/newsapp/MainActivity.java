package com.example.theblah.newsapp;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Void>, RecyclerViewAdapter.ItemClickListener {
    static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private Cursor cursor;
    private SQLiteDatabase db;
    private ProgressBar progress;
    //https://newsapi.org/v1/articles?source=the-next-web&sortBy=latest&apiKey=

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //check shared preferences for previous installation of app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirst = prefs.getBoolean("isfirst", true);

        if (isFirst) { //if first install then do initial loading of db
            refresh();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isfirst", false);
            editor.commit();
        } else {    //if is not first install show current info stored in db
            resetRV(this);
        }
        //schedule
        ScheduleUtils.scheduleRefresh(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showRV();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        resetRV(this);
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

    //hide rv and show progress circle
    private void loadingRV() {
        progress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    //hide progress circle and show rv
    private void showRV() {
        progress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    //reset recycler view with new cursor
    private void resetRV(Context context){
        if(db != null) db.close();
        if(cursor != null) cursor.close();

        //update db/cursor
        db = new DBUtils(context).getReadableDatabase();
        cursor = DBUtils.getAll(db);

        //reset rv to new cursor
        mAdapter = new RecyclerViewAdapter(cursor, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        showRV();
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
