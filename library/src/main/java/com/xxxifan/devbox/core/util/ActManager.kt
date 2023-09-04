package com.xxxifan.devbox.core.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

//
// Created by xxxifan on 2020/11/4.
//
object ActManager : Application.ActivityLifecycleCallbacks {
  @JvmStatic val activityList = arrayListOf<Activity>()

  /**
   * only one instance, till no other needs.
   */
  @JvmStatic var foregroundListener: ((Boolean) -> Unit)? = null

  private var foregroundCount = 0


  fun init(app: Application) {
    app.registerActivityLifecycleCallbacks(this)
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    activityList.add(activity)
  }

  override fun onActivityStarted(activity: Activity) {
    if (foregroundCount == 0) {
      foregroundListener?.invoke(true)
    }
    foregroundCount += 1
  }

  override fun onActivityResumed(activity: Activity) {
  }

  override fun onActivityPaused(activity: Activity) {
  }

  override fun onActivityStopped(activity: Activity) {
    foregroundCount -= 1
    if (foregroundCount <= 0) {
      foregroundCount = 0
      foregroundListener?.invoke(false)
    }
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
  }

  override fun onActivityDestroyed(activity: Activity) {
    activityList.remove(activity)
  }

  @JvmStatic fun appExit() {
    try {
      killAll()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * kill all activity.
   */
  @JvmStatic fun killAll() {
    val iterator: MutableIterator<Activity> = activityList.iterator()
    while (iterator.hasNext()) {
      val next = iterator.next()
      iterator.remove()
      next.finish()
    }
  }

  /**
   * check specify activity class is alive.
   */
  @JvmStatic fun <T : Activity> isActivityAlive(activityClass: Class<T>): Boolean {
    return activityList.any { activityClass == it.javaClass }
  }

  /**
   * kill specify activity class.
   */
  @JvmStatic fun <T : Activity> killActivity(activityClass: Class<T>) {
    val iterator: MutableIterator<Activity> = activityList.iterator()
    while (iterator.hasNext()) {
      val next = iterator.next()
      if(next::class.java == activityClass){
        iterator.remove()
        next.finish()
      }
    }
  }

  @JvmStatic fun <T : Activity> isActivityTop(
    activityClass: Class<T>,
    excludeCurrent: Boolean = false
  ): Boolean {
    val act = activityList.firstOrNull { it.javaClass == activityClass } ?: return false
    return activityList.indexOf(act) == if (excludeCurrent) activityList.size - 2 else activityList.size - 1
  }
}