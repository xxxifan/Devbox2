package com.xxxifan.devbox.core.base

import android.os.Bundle
import org.greenrobot.eventbus.EventBus

class EventBusModule {
  companion object {
    const val REGISTER_TAG = "EventBusModule_registerEventBus"
    const val KEEP_TAG = "EventBusModule_keepEventBusAlive"
  }
  private var registerEventBus = false
  private var keepEventBusAlive = false

  fun registerEventBus(obj: Any, keepalive: Boolean = false) {
    registerEventBus = true
    keepEventBusAlive = keepalive

    if (keepalive) {
      register(obj)
    }
  }

  fun onDestroy(obj: Any) {
    unregister(obj)
  }

  fun onResume(obj: Any) {
    if (!keepEventBusAlive && registerEventBus) {
      register(obj)
    }
  }

  fun onPause(obj: Any) {
    if (!keepEventBusAlive && registerEventBus) {
      unregister(obj)
    }
  }

  private fun register(obj: Any) {
    EventBus.getDefault().run {
      if (!isRegistered(obj)) { // 预先判断一次比直接调用register效率高很多
        register(obj)
      }
    }
  }

  private fun unregister(obj: Any) {
    EventBus.getDefault().run {
      if (isRegistered(obj)) {
        unregister(obj)
      }
    }
  }

  fun onSaveInstanceState(outState: Bundle?) {
    outState?.putBoolean(REGISTER_TAG, registerEventBus)
    outState?.putBoolean(KEEP_TAG, keepEventBusAlive)
  }

}