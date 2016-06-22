package com.xxxifan.devbox.library.util;

import android.content.SharedPreferences;

import com.xxxifan.devbox.library.AppPref;

/**
 * Created by xifan on 15-8-23.
 */
public class Once {

    public static final String TAG = "Once";

    private Once() {
    }

    /**
     * check a key for once.
     * @return isOnce
     */
    public static boolean check(String key) {
        SharedPreferences pref = AppPref.getPrefs(TAG);
        if (!pref.getBoolean(key, false)) {
            pref.edit().putBoolean(key, true).apply();
            return true;
        }
        return false;
    }

    /**
     * check a key for once, with callback way.
     */
    public static void check(String key, OnceCallback callback) {
        SharedPreferences pref = AppPref.getPrefs(TAG);
        if (!pref.getBoolean(key, false)) {
            if (callback != null) {
                callback.onOnce();
            }
            pref.edit().putBoolean(key, true).apply();
        }
    }

    public static void reset(String key) {
        SharedPreferences pref = AppPref.getPrefs(TAG);
        pref.edit().putBoolean(key, false).apply();
    }

    public interface OnceCallback{
        void onOnce();
    }
}
