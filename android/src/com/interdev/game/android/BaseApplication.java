package com.interdev.game.android;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.interdev.game.ParseCom;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

public class BaseApplication extends Application implements ParseCom {
    private Tracker globalTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "CrnAANwAmJsobh8Cex6tpsR370xedlLo4ZQKnifX", "DABa5FZuuc5jVmCW3ARTafz82eMzmOXWfRF8dnT1");
    }

    public Tracker getGlobalTracker() {
        if (globalTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            globalTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return globalTracker;
    }

    @Override
    public void incrementStats() {
        HashMap<String, Object> params = new HashMap<String, Object>();
            ParseCloud.callFunctionInBackground("incrementStats", params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    //Log.i("parse answer: ", result + "");
                } else {
                    //Log.e("PARSE: ", e.toString());
                }
            }
        });
    }
}
