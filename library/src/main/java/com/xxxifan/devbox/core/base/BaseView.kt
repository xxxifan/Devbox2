package com.xxxifan.devbox.core.base

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by xifan on 17-8-1.
 */
interface BaseView : LifecycleOwner {
  val disposables: CompositeDisposable
  val viewContext: Context?
    get() {
      return when (this) {
        is Activity -> this
        is Fragment -> context
        else -> null
      }
    }
}