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

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Process

@SuppressLint("StaticFieldLeak")
/**
 * Created by xifan on 17-9-2.
 */
object Devbox {
    lateinit var appDelegate: Context

    fun init(context: Context) {
        this.appDelegate = context
    }

    /**
     * Get app package info.
     */
    @Throws(PackageManager.NameNotFoundException::class)
    fun getPackageInfo(): PackageInfo {
        val manager = appDelegate.packageManager
        return manager.getPackageInfo(appDelegate.packageName, 0)
    }

    fun getVersionCode(): Long {
        return try {
            getPackageInfo().versionCode.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getVersionName(): String {
        return try {
            getPackageInfo().versionName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun isMainProcess(): Boolean {
        val am = appDelegate.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = am.runningAppProcesses
        val mainProcessName = appDelegate.packageName
        val myPid = Process.myPid()
        return processInfos.any { it.pid == myPid && mainProcessName == it.processName }
    }
}