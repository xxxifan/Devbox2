package com.xxxifan.devbox.library;

import android.content.Context;
import android.os.Build;

import com.xxxifan.devbox.library.util.http.Http;

import okhttp3.OkHttpClient;

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

    public static void setHttpClient(OkHttpClient client) {
        Http.initClient(client);
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
