package com.xxxifan.devbox.core.ext

import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> io() = ObservableTransformer<T, T> {
  it.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> computation() = ObservableTransformer<T, T> {
  it.subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> ioSingle() = SingleTransformer<T, T> {
  it.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> computationSingle() = SingleTransformer<T, T> {
  it.subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
}
