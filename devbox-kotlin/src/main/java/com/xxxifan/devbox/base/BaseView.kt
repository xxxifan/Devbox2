package com.xxxifan.devbox.base

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import com.xxxifan.devbox.Devbox

/**
 * Created by xifan on 17-8-1.
 */
interface BaseView<in T : BasePresenter<*>> {
    fun setPresenter(presenter: T?)
    fun showMessage(msg:String)
}

fun BaseView<*>.getContext(): Context {
    return when (this) {
        is Activity -> this
        is Fragment -> context
        else -> Devbox.appDelegate
    }
}