package com.example.theblah.newsapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.theblah.newsapp.Utility.DBUtils;

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
    synchronized public static void loadingRV() {
        progress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    //hide progress circle and show rv
    synchronized public static void showRV() {
        progress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    //reset recycler view with new cursor
    synchronized public static void resetRV(Context context){
        if(db != null) db.close();
        if(cursor != null) cursor.close();

        //update db/cursor
        db = new DBUtils(context).getReadableDatabase();
        cursor = DBUtils.getAll(db);

        //reset rv to new cursor
        mAdapter = new RecyclerViewAdapter(cursor, main);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        showRV();
    }
}
