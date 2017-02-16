package com.example.camera;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by reid on 2017/2/16.
 */

public class CameraApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);
    }
}
