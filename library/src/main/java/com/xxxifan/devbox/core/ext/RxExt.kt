package com.xxxifan.devbox.core.ext

import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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

/**
 * Add the disposable to a CompositeDisposable.
 * @param compositeDisposable CompositeDisposable to add this disposable to
 * @return this instance
 */
fun Disposable.addTo(compositeDisposable: CompositeDisposable?): Disposable =
  apply { compositeDisposable?.add(this) }