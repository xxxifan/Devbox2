package com.xxxifan.devbox.util

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.AnimRes
import android.support.annotation.CheckResult
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v4.util.ArrayMap
import android.support.v4.view.ViewPager
import android.view.View
import com.xxxifan.devbox.base.BasePresenter
import com.xxxifan.devbox.base.BaseView
import java.util.*

/**
 * Created by xifan on 6/7/16.
 */
@SuppressLint("RestrictedApi")
object Fragments {
    val TAG = "Fragments"
    val KEY_RESTORE = "restore"
    val KEY_RESTORE_VIEWPAGER = "restore_viewpager"
    val DEBUG = false

    private val REMAIN_POOL = ArrayMap<String, Int>()

    /**
     * checkout with [BaseActivity.FRAGMENT_CONTAINER].
     * it will use BaseFragment.getSimpleName() as tag, or SimpleClassName fallback.
     */
    @CheckResult
    fun checkout(activity: FragmentActivity, fragment: Fragment? = null,
                 tag: String? = null): Operator<FragmentActivity> {
        return Operator(activity, fragment, tag)
    }

    @CheckResult
    fun checkout(hostFragment: Fragment,
                 childFragment: Fragment? = null, tag: String? = null): Operator<Fragment> {
        return Operator(hostFragment, childFragment, tag)
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

    fun getFragmentList(activity: FragmentActivity): List<Fragment?> {
        return activity.supportFragmentManager.fragments ?: ArrayList()
    }

    fun getChildFragmentList(fragment: Fragment): List<Fragment?> {
        return fragment.childFragmentManager.fragments ?: ArrayList()
    }

    fun onSaveInstanceState(fragment: Fragment, outState: Bundle) {
        outState.putBoolean(KEY_RESTORE, fragment.isVisible)
        outState.putBoolean(KEY_RESTORE_VIEWPAGER, fragment.view?.parent is ViewPager)
    }

    fun restoreFragmentState(fragment: Fragment, outState: Bundle) {
        if (outState.getBoolean(KEY_RESTORE_VIEWPAGER, false)) return

        val transaction = fragment.fragmentManager.beginTransaction()
        if (outState.getBoolean(KEY_RESTORE, false)) {
            transaction.show(fragment)
        } else {
            transaction.hide(fragment)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun getTag(fragment: Fragment): String {
        return if (fragment.tag.isNullOrBlank()) fragment.javaClass.name else fragment.tag
    }

    /**
     * @param tag identify the host fragment will attach to. same tag will share one remain
     * * pool
     * *
     * @return the pool size left.
     */
    private fun consumeRemainPool(remainCount: Int, tag: String,
                                  totalCount: Int): Int {
        var count: Int? = REMAIN_POOL[tag]
        // null pool, initialize it
        if (count == null) {
            return if (remainCount > 0) {
                count = Math.max(remainCount - totalCount, 0)
                REMAIN_POOL.put(tag, count)
                count
            } else {
                -1
            }
        }

        // pool exist, consume it or not.
        return if (count-- > 0) {
            // consume pool
            REMAIN_POOL.put(tag, count)
            count
        } else {
            REMAIN_POOL.remove(tag)
            -1
        }
    }

    /**
     * get added fragments count in this container
     */
    private fun getAddedCount(fragments: List<Fragment?>, containerId: Int): Int {
        return fragments.count { it != null && it.isAdded && it.id == containerId }
    }

    @SuppressLint("CommitTransaction")
    class Operator<HostType>
    internal constructor(host: HostType,
                         private var fragment: Fragment? = null, private var tag: String? = null) {
        private var fragments: List<Fragment?>
        private var transaction: FragmentTransaction? = null
        private var hostTag: String

        // config field
        private var presenter: BasePresenter<Any>? = null
        private var addToBackStack: Boolean = false
        private var fade: Boolean = false
        private var removeLast: Boolean = false
        private var disableReuse: Boolean = false
        private var remainCount: Int = 0

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
                else -> throw RuntimeException("host must be android.support.v4.app.FragmentActivity or android.support.v4.app.Fragment")
            }

            transaction!!.setAllowOptimization(true)

            if (tag != null && fragment == null) { // retrieve correct fragment
                fragment = fragments.firstOrNull { it?.tag == tag }
            } else if (fragment != null) { // retrieve correct tag
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
        fun data(data: Bundle): Operator<HostType> {
            fragment?.arguments = data
            return this
        }

        /**
         * simple string bundle as argument
         */
        fun data(key: String, value: String?): Operator<HostType> {
            val bundle = fragment?.arguments ?: Bundle()
            bundle.putString(key, value)
            fragment?.arguments = bundle
            return this
        }

        fun data(key: String, value: Boolean): Operator<HostType> {
            val bundle = fragment?.arguments ?: Bundle()
            bundle.putBoolean(key, value)
            fragment?.arguments = bundle
            return this
        }

        fun data(key: String, value: Parcelable?): Operator<HostType> {
            val bundle = fragment?.arguments ?: Bundle()
            bundle.putParcelable(key, value)
            fragment?.arguments = bundle
            return this
        }

        fun addSharedElement(sharedElement: View, name: String): Operator<HostType> {
            transaction!!.addSharedElement(sharedElement, name)
            return this
        }

        fun setCustomAnimator(@AnimRes enter: Int, @AnimRes exit: Int): Operator<HostType> {
            transaction!!.setCustomAnimations(enter, exit)
            return this
        }

        fun setCustomAnimator(@AnimRes enter: Int, @AnimRes exit: Int,
                              @AnimRes popEnter: Int, @AnimRes popExit: Int): Operator<HostType> {
            transaction!!.setCustomAnimations(enter, exit, popEnter, popExit)
            return this
        }

        /**
         * Fragments use transaction optimization for better performance, if you face issues please
         * disable it.
         */
        fun disableOptimize(): Operator<HostType> {
            transaction!!.setAllowOptimization(false)
            return this
        }

        fun addToBackStack(): Operator<HostType> {
            this.addToBackStack = true
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
         * remove last fragment while checkout. it can remain a few of fragment for faster
         * recovery

         * @param remain the number that last fragment will remain
         */
        fun removeLast(remain: Int = 0): Operator<HostType> {
            this.removeLast = true
            this.remainCount = remain
            return this
        }

        /**
         * Fragments will reuse exists fragment when fragment tag is the same. Disable it will
         * force to use newly fragment instead of old one.
         */
        fun disableReuse(): Operator<HostType> {
            this.disableReuse = true
            return this
        }

        /**
         * @return success or not
         */
        fun into(@IdRes containerId: Int): Boolean {
            if (fragment == null) {
                commit()
                return false
            }

            val fragments = this.fragments
            val addedCount = getAddedCount(fragments, containerId)
            var canRemove = removeLast && consumeRemainPool(remainCount, hostTag, addedCount) < 0

            // hide or remove last fragment
            fragments.asSequence()
                    .filterNotNull()
                    .filter { it.id == containerId && it.isAdded }
                    .forEach {
                        if (it.tag == tag) {
                            if (!disableReuse) {
                                fragment = it // found previous, use old to keep data
                            } else {
                                transaction!!.remove(it) // or replace it
                            }
                        } else {
                            if (canRemove) {
                                transaction!!.remove(it)
                                canRemove = false
                            } else if (it.isVisible) {
                                transaction!!.hide(it)
                                it.userVisibleHint = false
                            }
                        }
                    }

            val canAddBackStack = transaction!!.isAddToBackStackAllowed && !transaction!!.isEmpty
            if (addToBackStack && canAddBackStack) {
                transaction!!.addToBackStack(tag)
            }

            if (fade) {
                transaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }

            if (!fragment!!.isAdded) {
                transaction!!.add(containerId, fragment, tag)
            }

            presenter?.let { (fragment as? BaseView<BasePresenter<Any>>)?.setPresenter(it) }

            transaction!!.show(fragment)

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
