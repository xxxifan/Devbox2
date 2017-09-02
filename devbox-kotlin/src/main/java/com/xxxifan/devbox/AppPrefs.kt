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

package com.xxxifan.devbox

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by xifan on 17-5-5.
 */
object AppPrefs {
    fun getPrefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(Devbox.appDelegate)
    }

    fun getPrefs(name: String): SharedPreferences {
        return Devbox.appDelegate.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun edit(): SharedPreferences.Editor {
        return getPrefs().edit()
    }

    fun putString(key: String, value: String) {
        getPrefs().edit().putString(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        getPrefs().edit().putInt(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        getPrefs().edit().putBoolean(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        getPrefs().edit().putLong(key, value).apply()
    }

    fun getString(key: String, defValue: String): String {
        return getPrefs().getString(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return getPrefs().getInt(key, defValue)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return getPrefs().getBoolean(key, defValue)
    }

    fun getLong(key: String, defValue: Long): Long {
        return getPrefs().getLong(key, defValue)
    }
}