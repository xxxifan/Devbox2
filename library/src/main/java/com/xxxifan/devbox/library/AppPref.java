package com.xxxifan.devbox.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xxxifan.devbox.library.base.Devbox;


/**
 * Created by xifan on 3/31/16.
 */
public class AppPref {

    private AppPref() {
    }

    public static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(Devbox.getAppDelegate());
    }

    public static SharedPreferences getPrefs(String name) {
        return Devbox.getAppDelegate().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor edit() {
        return getPrefs().edit();
    }

    public static void putString(String key, String value) {
        getPrefs().edit().putString(key, value).apply();
    }

    public static void putInt(String key, int value) {
        getPrefs().edit().putInt(key, value).apply();
    }

    public static void putBoolean(String key, boolean value) {
        getPrefs().edit().putBoolean(key, value).apply();
    }

    public static void putLong(String key, long value) {
        getPrefs().edit().putLong(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        return getPrefs().getString(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return getPrefs().getInt(key, defValue);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPrefs().getBoolean(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return getPrefs().getLong(key, defValue);
    }
}