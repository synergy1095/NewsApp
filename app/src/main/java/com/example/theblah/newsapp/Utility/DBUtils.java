package com.example.theblah.newsapp.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.theblah.newsapp.MainActivity;
import com.example.theblah.newsapp.models.NewsItem;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by TheBlah on 7/24/2017.
 *
 * Database helper methods
 */

public class DBUtils extends SQLiteOpenHelper {
    //db constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "newsapp.db";
    private static final String TAG = "DBUtils";

    public DBUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //added new columns finished and category
        //added IF NOT EXISTS for more safety
        String queryString = "CREATE TABLE IF NOT EXISTS " + Constants.NewsTable.TABLE_NAME + " ("+
                Constants.NewsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constants.NewsTable.COLUMN_NAME_AUTHOR + " TEXT NOT NULL, " +
                Constants.NewsTable.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                Constants.NewsTable.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                Constants.NewsTable.COLUMN_NAME_URL + " TEXT, " +
                Constants.NewsTable.COLUMN_NAME_IMAGE_URL + " TEXT, " +
                Constants.NewsTable.COLUMN_NAME_PUBLISHED_DATE + " DATE " +
                "); ";

        Log.d(TAG, "Create table SQL: " + queryString);
        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //added upgrade functionality to deal with upgrading from previous version of db
        db.execSQL("DROP TABLE IF EXISTS " + Constants.NewsTable.TABLE_NAME + ";");
        //redo check if table exists
        //added new columns finished and category
        String queryString = "CREATE TABLE IF NOT EXISTS " + Constants.NewsTable.TABLE_NAME + " ("+
                Constants.NewsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constants.NewsTable.COLUMN_NAME_AUTHOR + " TEXT NOT NULL, " +
                Constants.NewsTable.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                Constants.NewsTable.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                Constants.NewsTable.COLUMN_NAME_URL + " TEXT" +
                Constants.NewsTable.COLUMN_NAME_IMAGE_URL + " TEXT, " +
                Constants.NewsTable.COLUMN_NAME_PUBLISHED_DATE + " DATE, " +
                "); ";
        Log.d(TAG, "Create table SQL: " + queryString);
        db.execSQL(queryString);
    }

    //helper method to get new cursor with sorted by date
    public static Cursor getAll(SQLiteDatabase db) {
        Cursor cursor = db.query(
                Constants.NewsTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Constants.NewsTable.COLUMN_NAME_PUBLISHED_DATE + " DESC"
        );
        return cursor;
    }

    //clear table in prep for refresh
    public static void deleteAll(SQLiteDatabase db) {
        db.delete(Constants.NewsTable.TABLE_NAME, null, null);
    }

    //bulk insert article info to db using transaction to prevent multiple open/close for each
    public static void bulkInsert(SQLiteDatabase db, ArrayList<NewsItem> news) {
        db.beginTransaction();
        try {
            for (NewsItem item : news) {
                ContentValues cv = new ContentValues();
                cv.put(Constants.NewsTable.COLUMN_NAME_AUTHOR, item.getAuthor());
                cv.put(Constants.NewsTable.COLUMN_NAME_TITLE, item.getTitle());
                cv.put(Constants.NewsTable.COLUMN_NAME_DESCRIPTION, item.getDescription());
                cv.put(Constants.NewsTable.COLUMN_NAME_URL, item.getUrl());
                cv.put(Constants.NewsTable.COLUMN_NAME_IMAGE_URL, item.getUrlToImage());
                cv.put(Constants.NewsTable.COLUMN_NAME_PUBLISHED_DATE, item.getPublishedAt());
                db.insert(Constants.NewsTable.TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    //deletes data currently in db and then pulls new data from web and inserts new data
    synchronized public static void refreshDB(Context context){
        ArrayList<NewsItem> result = null;
        URL url = NetworkUtils.makeURL(Constants.SOURCE, Constants.SORTBY);

        SQLiteDatabase db = new DBUtils(context).getWritableDatabase();

        try {
            DBUtils.deleteAll(db);
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            result = jsonUtils.parseJSON(json);
            DBUtils.bulkInsert(db, result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
    }
}
