/*
 * Copyright  (c) 2016 xxxifan
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
