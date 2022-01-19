package com.xxxifan.devbox.core.base

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope

/**
 * Created by xifan on 17-8-1.
 */
interface BasePresenter<T> : DefaultLifecycleObserver {
  var view: T?

  val lifecycleOwner: LifecycleOwner?
    get() = view as? LifecycleOwner

  /**
   * coroutineScope from lifecycle, runs on Dispatchers.MAIN.IMMEDIATE
   */
  val lifecycleScope
    get() = lifecycleOwner?.lifecycle?.coroutineScope


  override fun onDestroy(owner: LifecycleOwner) {
    view = null
    owner.lifecycle.removeObserver(this)
  }

}