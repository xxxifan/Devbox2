package com.xxxifan.devbox.core.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.xxxifan.devbox.core.util.Fragments
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

  protected abstract val bind: ViewBinding

  private val eventBus: EventBusModule by lazy { EventBusModule() }
  private var firstVisible = true
  protected var currentTime=System.currentTimeMillis()
  protected open val disposables: CompositeDisposable by lazy { CompositeDisposable() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Fragments.restoreFragmentState(this, savedInstanceState)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    onSetupFragment(view, savedInstanceState)
    view.post {
      if (userVisibleHint) {
        onVisible()
        firstVisible = false
      }
    }
  }

  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    if (isVisibleToUser) {
      if (firstVisible) {
        firstVisible = false
      } else {
        onVisible()
      }
    } else {
      onHide()
    }
  }

  override fun onResume() {
    super.onResume()
    eventBus.onResume(this)
  }

  override fun onPause() {
    super.onPause()
    eventBus.onPause(this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    eventBus.onDestroy(this)
    disposables.dispose()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    Fragments.onSaveInstanceState(this, outState)
    eventBus.onSaveInstanceState(outState)
  }

  protected fun registerEventBus(keepalive: Boolean = false) {
    eventBus.registerEventBus(this, keepalive)
  }

  /**
   * onViewCreated()之后被调用，此时可以进行Fragment相关配置，初始化等工作
   */
  protected abstract fun onSetupFragment(view: View, savedInstanceState: Bundle?)

  open fun onVisible() {
  }

  open fun onHide() {
  }
}