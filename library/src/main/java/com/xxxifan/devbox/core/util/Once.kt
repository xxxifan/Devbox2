package com.xxxifan.devbox.core.util

import android.content.SharedPreferences


//
// Created by xxxifan on 2021/8/27.
//

/**
 * 检查是否第一次使用该key
 */
fun SharedPreferences.firstTime(key: String): Boolean {
  if (!getBoolean(key, false)) {
    edit().putBoolean(key, true).apply()
    return true
  }
  return false
}

fun SharedPreferences.resetFirstTime(key: String) {
  edit().putBoolean(key, true).apply()
}