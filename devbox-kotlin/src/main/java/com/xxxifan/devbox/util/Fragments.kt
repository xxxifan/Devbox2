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
import java.lang.ref.WeakReference
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
     * checkout with FRAGMENT_CONTAINER(which is defined in BaseActivity, is
     * R.id.fragment_container. it will use BaseFragment.getSimpleName() as tag, or SimpleClassName
     * if fallback.
     */
    @CheckResult
    fun checkout(activity: FragmentActivity,
                 fragment: Fragment): SingleOperator {
        return SingleOperator(activity, fragment)
    }

    /**
     * checkout with specified tag
     */
    @CheckResult
    fun checkout(activity: FragmentActivity, fragment: Fragment,
                 tag: String): SingleOperator {
        return SingleOperator(activity, fragment, tag)
    }

    /**
     * checkout previously fragment by tag
     */
    @CheckResult
    fun checkout(activity: FragmentActivity, tag: String): SingleOperator {
        return SingleOperator(activity, null, tag)
    }

    /**
     * checkout with FRAGMENT_CONTAINER(which is defined in BaseActivity, is
     * R.id.fragment_container.  it will use BaseFragment.getSimpleName() as tag, or
     * SimpleClassName
     * if fallback.
     */
    @CheckResult
    fun checkout(hostFragment: Fragment,
                 childFragment: Fragment): SingleChildOperator {
        return SingleChildOperator(hostFragment, childFragment)
    }

    /**
     * checkout with specified tag
     */
    @CheckResult
    fun checkout(hostFragment: Fragment,
                 childFragment: Fragment, tag: String): SingleChildOperator {
        return SingleChildOperator(hostFragment, childFragment, tag)
    }

    /**
     * checkout previously childFragment by tag
     */
    @CheckResult
    fun checkout(hostFragment: Fragment, tag: String): SingleChildOperator {
        return SingleChildOperator(hostFragment, null, tag)
    }

    /**
     * add multi fragments
     */
    @CheckResult
    fun add(activity: FragmentActivity, vararg fragments: Fragment): MultiOperator {
        return MultiOperator(activity, arrayOf(*fragments))
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
        return activity.supportFragmentManager.fragments ?: ArrayList<Fragment?>()
    }

    fun getChildFragmentList(fragment: Fragment): List<Fragment?> {
        return fragment.childFragmentManager.fragments ?: ArrayList<Fragment?>()
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
            if (remainCount > 0) {
                count = Math.max(remainCount - totalCount, 0)
                REMAIN_POOL.put(tag, count)
                return count
            } else {
                return -1
            }
        }

        // pool exist, consume it or not.
        if (count-- > 0) {
            // consume pool
            REMAIN_POOL.put(tag, count)
            return count
        } else {
            REMAIN_POOL.remove(tag)
            return -1
        }
    }

    /**
     * get added fragments count in this container
     */
    private fun getAddedCount(fragments: List<Fragment?>, containerId: Int): Int {
        return fragments.count { it != null && it.isAdded && it.id == containerId }
    }


    class SingleChildOperator @SuppressLint("CommitTransaction")
    internal constructor(private var hostFragment: Fragment?,
                         private var childFragment: Fragment? = null, private var tag: String? = null) {
        private var transaction: FragmentTransaction? = null
        private var presenter: BasePresenter<Any>? = null

        private var addToBackStack: Boolean = false
        private var fade: Boolean = false
        private var removeLast: Boolean = false
        private var disableReuse: Boolean = false
        private var remainCount: Int = 0

        init {
            transaction = hostFragment?.childFragmentManager?.beginTransaction()
            transaction?.setAllowOptimization(true)

            if (childFragment == null) {
                // retrieve correct childFragment
                val fragments = getChildFragmentList(hostFragment!!)
                this.childFragment = fragments.first { it?.tag != tag }
            }

            if (tag == null && childFragment != null) {
                tag = getTag(childFragment!!)
            }
        }

        fun <T : BasePresenter<*>?> bindPresenter(presenter: T?): SingleChildOperator {
            presenter?.let { this.presenter = it as BasePresenter<Any> }
            return this
        }

        /**
         * setArguments to target fragment.
         */
        fun data(data: Bundle): SingleChildOperator {
            childFragment?.arguments = data
            return this
        }

        /**
         * simple string bundle as argument
         */
        fun data(key: String, value: String?): SingleChildOperator {
            val bundle = childFragment?.arguments ?: Bundle()
            bundle.putString(key, value)
            childFragment?.arguments = bundle
            return this
        }

        fun data(key: String, value: Parcelable?): SingleChildOperator {
            val bundle = childFragment?.arguments ?: Bundle()
            bundle.putParcelable(key, value)
            childFragment?.arguments = bundle
            return this
        }

        fun addSharedElement(sharedElement: View, name: String): SingleChildOperator {
            transaction!!.addSharedElement(sharedElement, name)
            return this
        }

        fun setCustomAnimator(@AnimRes enter: Int, @AnimRes exit: Int): SingleChildOperator {
            transaction!!.setCustomAnimations(enter, exit)
            return this
        }

        fun setCustomAnimator(@AnimRes enter: Int, @AnimRes exit: Int,
                              @AnimRes popEnter: Int, @AnimRes popExit: Int): SingleChildOperator {
            transaction!!.setCustomAnimations(enter, exit, popEnter, popExit)
            return this
        }

        /**
         * Fragments use transaction optimization for better performance, if you face issues please
         * disable it.
         */
        fun disableOptimize(): SingleChildOperator {
            transaction!!.setAllowOptimization(false)
            return this
        }

        fun addToBackStack(): SingleChildOperator {
            this.addToBackStack = true
            return this
        }

        /**
         * display fade transition
         */
        fun fade(): SingleChildOperator {
            this.fade = true
            return this
        }

        /**
         * remove last fragment while checkout. it can remain a few of fragment for faster
         * recovery

         * @param remain the number that last fragment will remain
         */
        @JvmOverloads
        fun removeLast(remain: Int = 0): SingleChildOperator {
            this.removeLast = true
            this.remainCount = remain
            return this
        }

        /**
         * Fragments will reuse exists fragment when fragment tag is the same. Disable it will
         * force to use newly fragment instead of old one.
         */
        fun disableReuse(): SingleChildOperator {
            this.disableReuse = true
            return this
        }

        /**
         * @return success or not
         */

        fun into(@IdRes containerId: Int): Boolean {
            if (childFragment == null) {
                commit()
                return false
            }

            val fragments = getChildFragmentList(hostFragment!!)
            val hostTag = getTag(hostFragment!!)
            val addedCount = getAddedCount(fragments, containerId)
            var canRemove = removeLast && consumeRemainPool(remainCount, hostTag, addedCount) < 0

            // hide or remove last fragment
            fragments.asSequence()
                    .filterNotNull()
                    .filter { it.id == containerId && it.isAdded }
                    .forEach {
                        if (it.tag == tag) {
                            if (!disableReuse) {
                                childFragment = it // found previous, use old to keep data
                            } else {
                                transaction!!.remove(it)
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
            transaction?.let { transaction ->
                val canAddBackStack = transaction.isAddToBackStackAllowed && !transaction.isEmpty
                if (fade) {
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                }
                if (addToBackStack) {
                    if (canAddBackStack) {
                        transaction.addToBackStack(tag)
                    } else {
                    }
                }

                if (!childFragment!!.isAdded) {
                    transaction.add(containerId, childFragment, tag)
                }

                presenter?.let { (childFragment as? BaseView<BasePresenter<Any>>)?.setPresenter(it) }

                transaction.show(childFragment)

                commit()
                return true
            }
            return false
        }

        private fun commit() {
            transaction!!.commitAllowingStateLoss()
            transaction = null
            childFragment = null
            presenter = null
            hostFragment = null
        }
    }

    /**
     * remove last fragment while checkout.
     */

    class SingleOperator @SuppressLint("CommitTransaction")
    internal constructor(private var activity: FragmentActivity?,
                         private var fragment: Fragment? = null,
                         private var tag: String? = null) {
        private var presenter: BasePresenter<Any>? = null
        private var transaction: FragmentTransaction? = null

        private var addToBackStack: Boolean = false
        private var fade: Boolean = false
        private var removeLast: Boolean = false
        private var disableReuse: Boolean = false
        private var remainCount: Int = 0

        init {
            transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setAllowOptimization(true)

            if (fragment == null) {
                // retrieve correct fragment
                val fragments = getFragmentList(activity!!)
                this.fragment = fragments.first { it?.tag != tag }
            }

            if (tag == null && fragment != null) {
                tag = getTag(fragment!!)
            }
        }

        fun <T : BasePresenter<*>?> bindPresenter(presenter: T?): SingleOperator {
            presenter?.let { this.presenter = it as BasePresenter<Any> }
            return this
        }

        /**
         * setArguments to target fragment.
         */
        fun data(data: Bundle): SingleOperator {
            fragment?.arguments = data
            return this
        }

        /**
         * simple string bundle as argument
         */
        fun data(key: String, value: String?): SingleOperator {
            val bundle = fragment?.arguments ?: Bundle()
            bundle.putString(key, value)
            fragment?.arguments = bundle
            return this
        }

        fun data(key: String, value: Boolean): SingleOperator {
            val bundle = fragment?.arguments ?: Bundle()
            bundle.putBoolean(key, value)
            fragment?.arguments = bundle
            return this
        }

        fun data(key: String, value: Parcelable?): SingleOperator {
            val bundle = fragment?.arguments ?: Bundle()
            bundle.putParcelable(key, value)
            fragment?.arguments = bundle
            return this
        }

        fun addSharedElement(sharedElement: View, name: String): SingleOperator {
            transaction!!.addSharedElement(sharedElement, name)
            return this
        }

        fun setCustomAnimator(@AnimRes enter: Int, @AnimRes exit: Int): SingleOperator {
            transaction!!.setCustomAnimations(enter, exit)
            return this
        }

        fun setCustomAnimator(@AnimRes enter: Int, @AnimRes exit: Int,
                              @AnimRes popEnter: Int, @AnimRes popExit: Int): SingleOperator {
            transaction!!.setCustomAnimations(enter, exit, popEnter, popExit)
            return this
        }

        /**
         * Fragments use transaction optimization for better performance, if you face issues please
         * disable it.
         */
        fun disableOptimize(): SingleOperator {
            transaction!!.setAllowOptimization(false)
            return this
        }

        fun addToBackStack(): SingleOperator {
            this.addToBackStack = true
            return this
        }

        /**
         * display fade transition
         */
        fun fade(): SingleOperator {
            this.fade = true
            return this
        }

        /**
         * remove last fragment while checkout. it can remain a few of fragment for faster
         * recovery

         * @param remain the number that last fragment will remain
         */
        @JvmOverloads
        fun removeLast(remain: Int = 0): SingleOperator {
            this.removeLast = true
            this.remainCount = remain
            return this
        }

        /**
         * Fragments will reuse exists fragment when fragment tag is the same. Disable it will
         * force to use newly fragment instead of old one.
         */
        fun disableReuse(): SingleOperator {
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

            val fragments = getFragmentList(activity!!)
            val activityTag = activity!!.localClassName
            val addedCount = getAddedCount(fragments, containerId)
            var canRemove = removeLast && consumeRemainPool(remainCount, activityTag, addedCount) < 0

            // hide or remove last fragment
            fragments
                    .asSequence()
                    .filterNotNull()
                    .filter { it.id == containerId && it.isAdded }
                    .forEach {
                        if (it.tag == tag) {
                            if (!disableReuse) {
                                fragment = it // found previous, use old to keep data
                            } else {
                                transaction!!.remove(it)
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

            if (fade) {
                transaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }

            if (addToBackStack) {
                if (canAddBackStack) {
                    transaction!!.addToBackStack(tag)
                } else {
                }
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
            activity = null
        }
    }

    /**
     * remove last fragment while checkout.
     */

    // TODO: 6/10/16 MultiOperator is not used that much, so I only give it basic into function here.
    @SuppressLint("CommitTransaction")
    class MultiOperator(activity: FragmentActivity, val fragments: Array<Fragment>) {
        val activityRef: WeakReference<FragmentActivity> = WeakReference(activity)

        fun into(vararg ids: Int) {
            if (ids.size != fragments.size) {
                throw IllegalArgumentException("The length of ids and fragments is not equal.")
            }

            @SuppressLint("CommitTransaction")
            val transaction = activityRef.get()?.supportFragmentManager?.beginTransaction()
            transaction?.let { transaction ->
                fragments.forEachIndexed { index, fragment ->
                    transaction.replace(ids[index], fragment, getTag(fragment))
                }
                transaction.commitAllowingStateLoss()
            }
            activityRef.clear()
        }
    }
}
