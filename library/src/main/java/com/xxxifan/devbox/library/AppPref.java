/*
 * Copyright(c) 2016 xxxifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xxxifan.devbox.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by xifan on 3/31/16.
 */
public class AppPref {

    protected AppPref() {
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