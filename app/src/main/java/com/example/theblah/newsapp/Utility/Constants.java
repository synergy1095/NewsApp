package com.example.theblah.newsapp.Utility;

import android.provider.BaseColumns;

/**
 * Created by TheBlah on 7/24/2017.
 *
 * container for constants
 */

public class Constants {
    //loadermanager constants
    public static final int loaderID = 1;

    //website query constants
    public static final String SOURCE = "the-next-web";
    public static final String SORTBY = "latest";

    //firebase constants, all numbers in seconds with flextime providing execution window
    public static final int SCHEDULE_INTERVAL_MINUTES = 59;
    public static final int SYNC_FLEXTIME_SECONDS = 2;
    public static final String JOB_TAG = "newsapp_jtag";

    //sqlite table constants
    public static class NewsTable implements BaseColumns {
        public static final String TABLE_NAME = "articles";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
        public static final String COLUMN_NAME_PUBLISHED_DATE = "published_date";
    }
}
