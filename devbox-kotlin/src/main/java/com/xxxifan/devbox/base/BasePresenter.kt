package com.xxxifan.devbox.base

/**
 * Created by xifan on 17-8-1.
 */
interface BasePresenter<T> {
    var view: T
    fun onDestroy()
}