package com.xxxifan.devbox.core.base

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import io.reactivex.disposables.CompositeDisposable

/**
 * activity 封装类 用于管理
 */
abstract class BaseActivity : AppCompatActivity() {

  protected abstract val bind: ViewBinding

  private val eventBus: EventBusModule by lazy { EventBusModule() }
  private val toolbarModule by lazy { ToolbarModule() }
  private var backListener: (() -> Boolean)? = null

  protected val TAG = javaClass.simpleName

  val disposables: CompositeDisposable by lazy { CompositeDisposable() }

  override fun onCreate(savedInstanceState: Bundle?) {
    onPreCreate(savedInstanceState)
    super.onCreate(savedInstanceState)

    setContentView(bind.root)

    onSetupActivity(savedInstanceState)
  }

  override fun onResume() {
    super.onResume()
    eventBus.onResume(this)
  }

  override fun onPause() {
    super.onPause()
    eventBus.onPause(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    backListener = null
    eventBus.onDestroy(this)
    disposables.dispose()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    eventBus.onSaveInstanceState(outState)
  }

  @CallSuper
  override fun onBackPressed() {
    if (supportFragmentManager.backStackEntryCount > 0) {
      super.onBackPressed()
      onPopFragment()
    } else if (backListener?.invoke() != true) {
      super.onBackPressed()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  /**
   * 注册EventBus
   * @param keepalive 无视生命周期，在Activity被销毁前一直保持注册状态，否则会在onPause时自动取消注册
   */
  protected fun registerEventBus(keepalive: Boolean = false) {
    eventBus.registerEventBus(this, keepalive)
  }

  /**
   * 注册返回键事件
   * @param listener 返回true即消费本次事件不返回super，返回false即执行super call(退出)
   */
  protected fun registerBackKey(listener: () -> Boolean) {
    backListener = listener
  }

  protected fun useToolbarModule(): ToolbarModule {
    return toolbarModule
  }

  /**
   * 在super.onCreate()之前调用，用于特殊的activity配置请求
   */
  protected open fun onPreCreate(savedInstanceState: Bundle?) {
    supportActionBar?.setDisplayHomeAsUpEnabled(!isTaskRoot)
  }

  /**
   * 当setContentView()之后被调用，此时可以进行Activity相关配置，初始化等工作
   */
  protected abstract fun onSetupActivity(savedInstanceState: Bundle?)

  protected open fun onPopFragment() {
  }
}