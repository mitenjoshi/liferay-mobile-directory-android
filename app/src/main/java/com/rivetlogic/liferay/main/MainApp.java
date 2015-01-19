package com.rivetlogic.liferay.main;

import android.app.Application;
import android.content.Context;

/**
 * Copyright (c) 2014 HMR Weight Management Services Corp.. All rights reserved.  10/9/14.
 */

//http://thomas.lorenz@git.rivetlogic.com:7990/scm/hmr/hmr-android.git

public class MainApp extends Application {

    private static Context context;

    public MainApp() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }

}