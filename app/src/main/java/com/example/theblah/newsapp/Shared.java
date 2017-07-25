package com.example.theblah.newsapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by TheBlah on 7/24/2017.
 */

public class Shared {
    //service has no normal way of resetting the recycler view
    //static variables set here to allow asynctask in service to reset rv
    public static RecyclerView recyclerView;
    public static RecyclerViewAdapter mAdapter;
    public static Cursor cursor;
    public static SQLiteDatabase db;
    public static ProgressBar progress;
    public static RecyclerViewAdapter.ItemClickListener main;

    //hide rv and show progress circle
    public static void loadingRV() {
        progress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    //hide progress circle and show rv
    public static void showRV() {
        progress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
