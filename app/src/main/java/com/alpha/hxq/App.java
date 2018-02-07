package com.alpha.hxq;

import android.app.Application;

/**
 * Created by hukang on 2017/10/14.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

    }
    private static App app;

    public static App getInstance() {
        return app;
    }
}
