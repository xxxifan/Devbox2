package com.xxxifan.devbox.core.ext

import com.google.gson.Gson
import java.io.Reader
import java.lang.reflect.Type

/**
 * Created by xifan on 18-4-8.
 */

/**
 * Global gson instance, configure it when need.
 */
val gson = Gson()

fun Any.toJson(): String {
  if (this is String) {
    return this
  }
  return gson.toJson(this)
}

fun <T> String.fromJson(clazz: Class<T>) = gson.fromJson(this, clazz)
fun <T> String.fromJson(type: Type) = gson.fromJson<T>(this, type)
fun <T> Reader.fromJson(clazz: Class<T>) = gson.fromJson(this, clazz)
fun <T> Reader.fromJson(type: Type) = gson.fromJson<T>(this, type)