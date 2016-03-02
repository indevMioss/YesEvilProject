package com.interdev.game.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.interdev.game.GameMain;


public class AndroidLauncher extends AndroidApplication {

    private BaseApplication baseApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseApplication = (BaseApplication) getApplication();
        Tracker globalTracker = baseApplication.getGlobalTracker();

        globalTracker.setScreenName("com.interdev.motio.android.AndroidLauncher");
        globalTracker.send(new HitBuilders.ScreenViewBuilder().build());

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useWakelock = true;
        config.useImmersiveMode = true;
        config.hideStatusBar = true;

        initialize(new GameMain(baseApplication), config);
    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(baseApplication).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(baseApplication).reportActivityStop(this);
    }

}
