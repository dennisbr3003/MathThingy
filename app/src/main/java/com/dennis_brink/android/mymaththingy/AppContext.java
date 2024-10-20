package com.dennis_brink.android.mymaththingy;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

// https://stackoverflow.com/questions/7144177/getting-the-application-context

public class AppContext extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }


}