package com.xxxifan.devbox.core.util

import android.annotation.SuppressLint
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject

//
// Created by xxxifan on 9/27/19.
//
class Variable<T : Any>(data: T) {
  private val subject = BehaviorSubject.createDefault<T>(data)
  var value: T = data
    set(new) {
      if (new != field) {
        subject.onNext(new)
      }
      field = new
    }

  fun get(): T = value

  @SuppressLint("CheckResult") fun subscribe(onNext: Consumer<T>) {
    subject.subscribe(onNext, Consumer<Throwable?> { it?.printStackTrace() })
  }
}