package com.xxxifan.devbox.core

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

    @JvmStatic fun init(context: Context) {
        this.appDelegate = context
    }

    /**
     * Get app package info.
     */
    @Throws(PackageManager.NameNotFoundException::class)
    @JvmStatic fun getPackageInfo(): PackageInfo {
        val manager = appDelegate.packageManager
        return manager.getPackageInfo(appDelegate.packageName, 0)
    }

    @JvmStatic fun getVersionCode(): Long {
        return try {
            getPackageInfo().versionCode.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    @JvmStatic fun getVersionName(): String {
        return try {
            getPackageInfo().versionName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic fun isMainProcess(): Boolean {
        val am = appDelegate.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = am.runningAppProcesses
        val mainProcessName = appDelegate.packageName
        val myPid = Process.myPid()
        return processInfos.any { it.pid == myPid && mainProcessName == it.processName }
    }
}