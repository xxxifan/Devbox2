package com.xxxifan.devbox.core.util

import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject

//
// Created by xxxifan on 9/27/19.
//
class Variable<T>(data: T) {
  private val subject = BehaviorSubject.createDefault<T>(data)
  var value: T = data
    set(new) {
      if (new != field) {
        subject.onNext(new)
      }
      field = new
    }

  fun get(): T = value

  fun subscribe(onNext: Consumer<T>) {
    subject.subscribe(onNext, Consumer<Throwable?> { it?.printStackTrace() })
  }
}