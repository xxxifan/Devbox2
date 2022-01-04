package com.xxxifan.devbox.core.ext

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator


//
// Created by xxxifan on 2021/8/6.
//
/**
 * 动画结束淡出配置
 */
fun View.fadeOut(duration: Long = 300) {
  animate()
    .alpha(0f).setInterpolator(AccelerateDecelerateInterpolator()).setDuration(duration)
    .withEndAction { this@fadeOut.visibility = View.GONE }
    .start()
}

/**
 * 动画进入配置
 */
fun View.fadeIn(duration: Long = 300) {
  alpha = 0f
  animate()
    .alpha(1f).setInterpolator(AccelerateInterpolator()).setDuration(duration)
    .withStartAction {
      this@fadeIn.visibility = View.VISIBLE
    }
    .start()
}
