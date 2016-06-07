package com.xxxifan.devbox.library;

import android.content.Context;

/**
 * Created by xifan on 3/30/16.
 */
public class Devbox {

    private volatile static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    public static Context getAppDelegate() {
        if (sContext == null) {
            throw new IllegalStateException("Application instance is null, please check you have " +
                    "correct config");
        }
        return sContext;
    }
}
