package com.xxxifan.devbox.core.util

import android.provider.MediaStore


//
// Created by xxxifan on 2021/6/7.
//
class MediaFileInfo : MediaStore.MediaColumns {
  var id: String? = null
  var displayName: String? = null
  var size: Long = 0
  var dateModified: Long = 0
}