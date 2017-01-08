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

package com.xxxifan.devbox.core;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import java.util.List;

/**
 * Created by xifan on 3/30/16.
 */
public class Devbox {

    private static Context sContext;

    private Devbox() {}

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

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Get app package info.
     */
    public static PackageInfo getPackageInfo() throws PackageManager.NameNotFoundException {
        PackageManager manager = Devbox.getAppDelegate().getPackageManager();
        return manager.getPackageInfo(Devbox.getAppDelegate().getPackageName(), 0);
    }

    public static long getVersionCode() {
        try {
            return getPackageInfo().versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName() {
        try {
            return getPackageInfo().versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getAppDelegate().getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getAppDelegate().getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
