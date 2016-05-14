package com.xxxifan.devbox.library.base;

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
        return sContext;
    }
}
