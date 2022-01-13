package com.xxxifan.devbox.core.ext

import android.view.View
import com.xxxifan.devbox.core.R

//
// Created by xxxifan on 2022/1/13.
//

/**
 * 使用view的tag来存储上次点击时间，用来防止过快点击。
 * @param resetTime 如不需要在点击之后重新计时，需要设为false
 */
fun View.throttleClick(resetTime: Boolean = true, interval: Long = 300L): Boolean {
  val checkTime = System.currentTimeMillis()
  return if (getTag(R.id.viewClickTag) == null) {
    setTag(R.id.viewClickTag, checkTime)
    false
  } else {
    if (checkTime - (getTag(R.id.viewClickTag) as Long) < interval) {
      if (resetTime) {
        setTag(R.id.viewClickTag, checkTime)
      }
      true
    } else {
      setTag(R.id.viewClickTag, checkTime)
      false
    }
  }
}