package com.xxxifan.devbox.core.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.coroutineScope

/**
 * Created by xifan on 17-8-1.
 */
interface BasePresenter<T> : LifecycleObserver {
  var view: T?

  val lifecycleOwner: LifecycleOwner?
    get() = view as? LifecycleOwner

  /**
   * coroutineScope from lifecycle, runs on Dispatchers.MAIN.IMMEDIATE
   */
  val lifecycleScope
    get() = lifecycleOwner?.lifecycle?.coroutineScope

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy(owner: LifecycleOwner) {
    view = null
    owner.lifecycle.removeObserver(this)
  }

}