package com.example.theblah.newsapp.Utility;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.theblah.newsapp.MainActivity;
import com.example.theblah.newsapp.RecyclerViewAdapter;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import static com.example.theblah.newsapp.Shared.cursor;
import static com.example.theblah.newsapp.Shared.db;
import static com.example.theblah.newsapp.Shared.mAdapter;
import static com.example.theblah.newsapp.Shared.main;
import static com.example.theblah.newsapp.Shared.recyclerView;
import static com.example.theblah.newsapp.Shared.showRV;
import static com.example.theblah.newsapp.Utility.Constants.SCHEDULE_INTERVAL_MINUTES;
import static com.example.theblah.newsapp.Utility.Constants.SYNC_FLEXTIME_SECONDS;

/**
 * Created by TheBlah on 7/24/2017.
 *
 * service class for firebase use
 */

public class ScheduleUtils extends JobService {
    AsyncTask currentTask;
    private static boolean sInitialized = false;
    private static final String TAG = "ScheduleUtils";
    @Override
    public boolean onStartJob(final JobParameters job) {
        currentTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                Log.d(TAG, "scheduled refresh starting.");
                super.onPreExecute();
            }

            //refresh rv on finish
            @Override
            protected void onPostExecute(Object o) {
                //get cursor to updated db
                db = new DBUtils(ScheduleUtils.this).getReadableDatabase();
                cursor = DBUtils.getAll(db);

                //reset adapter to new cursor
                mAdapter = new RecyclerViewAdapter(cursor, main);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                jobFinished(job, false);
                super.onPostExecute(o);
            }

            //background thread do database refresh
            @Override
            protected Object doInBackground(Object[] params) {
                DBUtils.refreshDB(ScheduleUtils.this);
                return null;
            }
        };

        currentTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(currentTask != null) currentTask.cancel(false);
        return true;
    }

    //use firebase to schedule
    synchronized public static void scheduleRefresh(@NonNull final Context context){
        //check if already scheduled
        if(sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //build job using firebase dispatcher
        Job constraintRefreshJob = dispatcher.newJobBuilder()
                .setService(ScheduleUtils.class)
                .setTag(Constants.JOB_TAG)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(SCHEDULE_INTERVAL_MINUTES,
                        SCHEDULE_INTERVAL_MINUTES + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(constraintRefreshJob);
        //set to true to prevent other schedule attempts
        sInitialized = true;
    }
}
