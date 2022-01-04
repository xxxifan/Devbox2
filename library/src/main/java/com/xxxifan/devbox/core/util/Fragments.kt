package com.xxxifan.devbox.core.util

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.xxxifan.devbox.core.base.BasePresenter
import com.xxxifan.devbox.core.base.BaseView

/**
 * Created by xifan on 6/7/16.
 */
@SuppressLint("RestrictedApi")
object Fragments {
  const val TAG = "Fragments"
  private const val KEY_RESTORE = "restore"
  private const val KEY_RESTORE_VIEWPAGER = "restore_viewpager"

  /**
   * checkout fragment into container layout.
   * it will use BaseFragment.getSimpleName() as tag, or Class.name as fallback. For more see
   * [Operator]
   */
  @JvmOverloads
  @CheckResult
  fun checkout(
    activity: FragmentActivity, fragment: Fragment? = null,
    tag: String? = null
  ): Operator<FragmentActivity> {
    return Operator(activity, fragment, tag)
  }

  /**
   * [checkout] for fragment host
   */
  @JvmOverloads
  @CheckResult
  fun checkout(
    hostFragment: Fragment,
    childFragment: Fragment? = null, tag: String? = null
  ): Operator<Fragment> {
    return Operator(hostFragment, childFragment, tag)
  }

  fun hide(hostActivity: FragmentActivity, fragments: Array<Fragment>) {
    val transac = hostActivity.supportFragmentManager.beginTransaction()
    fragments.forEach {
      transac.hide(it)
      it.userVisibleHint = false
    }
    transac.commitAllowingStateLoss()
  }

  fun hide(hostFragment: Fragment, fragments: Array<Fragment>) {
    val trans = hostFragment.childFragmentManager.beginTransaction()
    fragments.forEach {
      trans.hide(it)
      it.userVisibleHint = false
    }
    trans.commitAllowingStateLoss()
  }

  /**
   * get current visible fragment on container
   */
  fun getCurrentFragment(activity: FragmentActivity, containerId: Int): Fragment? {
    return activity.supportFragmentManager.findFragmentById(containerId)
  }

  fun getFragment(activity: FragmentActivity, tag: String): Fragment? {
    return activity.supportFragmentManager.findFragmentByTag(tag)
  }

  fun getFragmentList(activity: FragmentActivity): List<Fragment> {
    return activity.supportFragmentManager.fragments
  }

  fun getChildFragmentList(fragment: Fragment): List<Fragment> {
    return fragment.childFragmentManager.fragments
  }

  fun onSaveInstanceState(fragment: Fragment, outState: Bundle) {
    outState.putBoolean(KEY_RESTORE, fragment.isVisible)
    outState.putBoolean(KEY_RESTORE_VIEWPAGER, fragment.view?.parent is ViewPager)
  }

  fun restoreFragmentState(fragment: Fragment, outState: Bundle?) {
    if (outState == null) return
    if (outState.getBoolean(KEY_RESTORE_VIEWPAGER, false)) return
    if (!fragment.isAdded) return
    val transaction = fragment.parentFragmentManager.beginTransaction()

    if (outState.getBoolean(KEY_RESTORE, false)) {
      transaction.show(fragment)
    } else {
      transaction.hide(fragment)
    }
    transaction.commitAllowingStateLoss()
  }

  private fun getTag(fragment: Fragment): String {
    return if (fragment.tag.isNullOrBlank()) fragment.javaClass.name else fragment.tag ?: ""
  }

  @SuppressLint("CommitTransaction")
  class Operator<HostType>
  internal constructor(
    host: HostType,
    private var fragment: Fragment? = null, private var tag: String? = null
  ) {
    private var fragments: List<Fragment?>
    private var transaction: FragmentTransaction? = null
    private var hostTag: String

    // config field
    private var presenter: BasePresenter<Any>? = null
    private var addToBackStack = false
    private var backStackTag: String? = null
    private var fade = false
    private var retainInstance = false

    init {
      val hostActivity = host as? FragmentActivity
      val hostFragment = host as? Fragment
      when {
        hostActivity != null -> {
          transaction = hostActivity.supportFragmentManager.beginTransaction()
          fragments = getFragmentList(hostActivity)
          hostTag = hostActivity.localClassName
        }
        hostFragment != null -> {
          transaction = hostFragment.childFragmentManager.beginTransaction()
          fragments = getChildFragmentList(hostFragment)
          hostTag = getTag(hostFragment)
        }
        else -> throw RuntimeException(
          "host must be androidx.fragment.app.FragmentActivity or androidx.fragment.app.Fragment"
        )
      }

//      transaction!!.setReorderingAllowed(true)

      if (tag?.isNotBlank() == true && fragment == null) { // retrieve correct fragment
        fragment = fragments.firstOrNull { it?.tag == tag }
      } else if (tag.isNullOrBlank() && fragment != null) { // retrieve correct tag
        tag = getTag(fragment!!)
      }
    }

    fun <T : BasePresenter<*>?> bindPresenter(presenter: T?): Operator<HostType> {
      presenter?.let { this.presenter = it as BasePresenter<Any> }
      return this
    }

    /**
     * setArguments to target fragment.
     */
    fun data(data: Bundle?): Operator<HostType> {
      if (fragment?.isStateSaved == true) return this // make sure fragment state.
      data ?: return this

      fragment?.arguments = data
      return this
    }

    /**
     * simple string bundle as argument
     */
    fun data(key: String, value: String?): Operator<HostType> {
      if (fragment?.isStateSaved == true) return this // make sure fragment state.

      val bundle = fragment?.arguments ?: Bundle()
      bundle.putString(key, value)
      fragment?.arguments = bundle
      return this
    }

    fun data(key: String, value: Boolean): Operator<HostType> {
      if (fragment?.isStateSaved == true) return this // make sure fragment state.

      val bundle = fragment?.arguments ?: Bundle()
      bundle.putBoolean(key, value)
      fragment?.arguments = bundle
      return this
    }

    fun data(key: String, value: Parcelable?): Operator<HostType> {
      if (fragment?.isStateSaved == true) return this // make sure fragment state.

      val bundle = fragment?.arguments ?: Bundle()
      bundle.putParcelable(key, value)
      fragment?.arguments = bundle
      return this
    }

    /**
     * see [FragmentTransaction.addSharedElement]
     */
    fun addSharedElement(sharedElement: View, name: String): Operator<HostType> {
      transaction!!.addSharedElement(sharedElement, name)
      return this
    }

    /**
     * see [FragmentTransaction.setCustomAnimations]
     */
    fun setCustomAnimator(@AnimRes enter: Int, @AnimRes exit: Int): Operator<HostType> {
      transaction!!.setCustomAnimations(enter, exit)
      return this
    }

    /**
     * see [FragmentTransaction.setCustomAnimations]
     */
    fun setCustomAnimator(
      @AnimRes enter: Int, @AnimRes exit: Int,
      @AnimRes popEnter: Int, @AnimRes popExit: Int
    ): Operator<HostType> {
      transaction!!.setCustomAnimations(enter, exit, popEnter, popExit)
      return this
    }

    /**
     * Fragments use transaction optimization for better performance, if you face issues please
     * disable it.
     */
//    fun disableOptimize(): Operator<HostType> {
//      transaction!!.setAllowOptimization(false)
//      return this
//    }

    fun addToBackStack(tag: String? = null): Operator<HostType> {
      this.addToBackStack = true
      this.backStackTag = tag
      return this
    }

    /**
     * make fragment call [Fragment.setRetainInstance(true)]
     */
    fun retainInstance(): Operator<HostType> {
      this.retainInstance = true
      return this
    }

    /**
     * display fade transition
     */
    fun fade(): Operator<HostType> {
      this.fade = true
      return this
    }

    /**
     * @return success or not
     */
    fun into(@IdRes containerId: Int): Boolean {
      val fragment = this.fragment
      if (fragment == null) {
        commit()
        return false
      }

      // use local val for easier access
      val fragments = this.fragments
      val transaction = this.transaction!!

      // hide other fragment
      fragments.asSequence()
        .filter { it != null && it.id == containerId && it.isAdded }
        .forEach {
          if (it!!.tag != tag && it.isVisible) {
            transaction.hide(it)
            it.userVisibleHint = false
          }
        }

      val canAddBackStack = transaction.isAddToBackStackAllowed && !transaction.isEmpty
      if (addToBackStack && canAddBackStack) {
        transaction.addToBackStack(backStackTag)
      }
      if (fade) {
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
      }
      if (!fragment.isAdded) {
        transaction.add(containerId, fragment, tag)
      }
      if (retainInstance) {
        fragment.retainInstance = true
      }
      presenter?.view = (fragment as? BaseView)

      fragment.userVisibleHint = true
      transaction.show(fragment)

      commit()
      return true
    }

    private fun commit() {
      transaction!!.commitAllowingStateLoss()
      transaction = null
      fragment = null
      presenter = null
    }
  }
}

fun Fragment.hide() {
  if (!isAdded) return
  val trans = parentFragmentManager.beginTransaction()
  trans.hide(this)
  trans.commit()
}
fun Fragment.remove() {
  if (!isAdded) return
  val trans = parentFragmentManager.beginTransaction()
  trans.remove(this)
  trans.commit()
}