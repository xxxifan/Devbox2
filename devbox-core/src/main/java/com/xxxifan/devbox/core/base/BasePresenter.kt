package com.xxxifan.devbox.core.base

/**
 * Created by xifan on 17-8-1.
 */
interface BasePresenter<T> {
    var view: T
    fun onDestroy()
}