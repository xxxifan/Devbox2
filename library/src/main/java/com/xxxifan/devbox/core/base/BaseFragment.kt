package com.xxxifan.devbox.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.xxxifan.devbox.core.ext.fragment.FragmentViewBindingDelegate
import com.xxxifan.devbox.core.util.Fragments

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

  protected abstract val bind: ViewBinding

  protected val baseActivity: BaseActivity?
    get() = activity as? BaseActivity

  var rootView: View? = null

  private val eventBus: EventBusModule by lazy { EventBusModule() }
  private var firstVisible = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Fragments.restoreFragmentState(this, savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return if (rootView != null) {
      val parent = rootView!!.parent
      if (parent != null) {
        (parent as ViewGroup).removeView(view)
      }
      rootView
    } else {
      super.onCreateView(inflater, container, savedInstanceState)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    rootView = view
    onSetupFragment(view, savedInstanceState)
    view.post {
      if (userVisibleHint) {
        onVisible()
        firstVisible = false
      }
    }
  }

  @Deprecated("Deprecated in Java")
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

  protected fun <T : ViewBinding> viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)
}