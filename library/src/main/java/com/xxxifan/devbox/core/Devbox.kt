package com.xxxifan.devbox.core

import android.app.Application

//
// Created by xxxifan on 2021/12/31.
//
object Devbox {
  @JvmStatic
  lateinit var appRef: Application

  @JvmStatic
  var debug: Boolean = true
}