/*
 * Copyright(c) 2016 xxxifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xxxifan.devbox.core.util;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.xxxifan.devbox.core.base.BaseActivity;
import com.xxxifan.devbox.core.base.BaseFragment;
import com.xxxifan.devbox.core.base.BasePresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xifan on 6/7/16.
 */
public final class Fragments {
    public static final String TAG = "Fragments";
    public static final String KEY_RESTORE = "restore";
    public static final String KEY_RESTORE_VIEWPAGER = "restore_viewpager";

    private static final ArrayMap<String, Integer> REMAIN_POOL = new ArrayMap<>();

    private Fragments() {
    }

    /**
     * checkout with FRAGMENT_CONTAINER(which is defined in BaseActivity, is
     * R.id.fragment_container. it will use BaseFragment.getSimpleName() as tag, or SimpleClassName
     * if fallback.
     */
    @CheckResult public static SingleOperator checkout(FragmentActivity activity,
            Fragment fragment) {
        return new SingleOperator(activity, fragment);
    }

    /**
     * checkout with specified tag
     */
    @CheckResult public static SingleOperator checkout(FragmentActivity activity, Fragment fragment,
            String tag) {
        return new SingleOperator(activity, fragment, tag);
    }

    /**
     * checkout previously fragment by tag
     */
    @CheckResult public static SingleOperator checkout(FragmentActivity activity, String tag) {
        return new SingleOperator(activity, tag);
    }

    /**
     * checkout with FRAGMENT_CONTAINER(which is defined in BaseActivity, is
     * R.id.fragment_container.  it will use BaseFragment.getSimpleName() as tag, or
     * SimpleClassName
     * if fallback.
     */
    @CheckResult public static SingleChildOperator checkout(Fragment hostFragment,
            Fragment childFragment) {
        return new SingleChildOperator(hostFragment, childFragment);
    }

    /**
     * checkout with specified tag
     */
    @CheckResult public static SingleChildOperator checkout(Fragment hostFragment,
            Fragment childFragment, String tag) {
        return new SingleChildOperator(hostFragment, childFragment, tag);
    }

    /**
     * checkout previously childFragment by tag
     */
    @CheckResult public static SingleChildOperator checkout(Fragment hostFragment, String tag) {
        return new SingleChildOperator(hostFragment, tag);
    }

    /**
     * add multi fragments
     */
    @CheckResult public static MultiOperator add(FragmentActivity activity, Fragment... fragments) {
        if (fragments == null) {
            throw new IllegalArgumentException("Can't accept null fragments");
        }
        return new MultiOperator(activity, fragments);
    }

    /**
     * get current visible fragment on container
     */
    public static Fragment getCurrentFragment(FragmentActivity activity, int containerId) {
        return activity.getSupportFragmentManager().findFragmentById(containerId);
    }

    public static Fragment getFragment(FragmentActivity activity, String tag) {
        return activity.getSupportFragmentManager().findFragmentByTag(tag);
    }

    @NonNull public static List<Fragment> getFragmentList(FragmentActivity activity) {
        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
        return fragments == null ? new ArrayList<Fragment>() : fragments;
    }

    @NonNull public static List<Fragment> getChildFragmentList(Fragment fragment) {
        List<Fragment> fragments = fragment.getChildFragmentManager().getFragments();
        return fragments == null ? new ArrayList<Fragment>() : fragments;
    }

    private static String getTag(Fragment fragment) {
        return Strings.isEmpty(fragment.getTag()) ? (fragment instanceof BaseFragment
                ? ((BaseFragment) fragment).getSimpleName() : fragment.getClass().getName())
                : fragment.getTag();
    }

    /**
     * @param tag identify the host fragment will attach to. same tag will share one remain
     * pool
     * @return the pool size left.
     */
    private static int consumeRemainPool(final int remainCount, final String tag,
            final int totalCount) {
        Integer count = REMAIN_POOL.get(tag);
        Logger.d("remain " + remainCount + ", get " + count + " total " + totalCount);
        // null pool, initialize it
        if (count == null) {
            if (remainCount > 0) {
                count = Math.max(remainCount - totalCount, 0);
                REMAIN_POOL.put(tag, count);
                Logger.d("initialize remain pool " + count);
                return count;
            } else {
                Logger.d("remain pool not available, return true");
                return -1;
            }
        }

        // pool exist, consume it or not.
        if (count-- > 0) {
            Logger.d((count + 1) + " count > 0, consume it");
            // consume pool
            REMAIN_POOL.put(tag, count);
            return count;
        } else {
            Logger.d("count < 0, pool reached, return true");
            REMAIN_POOL.remove(tag);
            return -1;
        }
    }


    /**
     * get added fragments count in this container
     */
    private static int getAddedCount(List<Fragment> fragments, int containerId) {
        int count = 0;
        Fragment fragment;
        for (int i = 0, s = fragments.size(); i < s; i++) {
            fragment = fragments.get(i);
            if (fragment != null && fragment.isAdded() && fragment.getId() == containerId) {
                count++;
            }
        }
        return count;
    }


    public static final class SingleChildOperator {
        private Fragment hostFragment;
        @Nullable private Fragment childFragment;
        private FragmentTransaction transaction;
        private String tag;
        private BasePresenter presenter;

        private boolean addToBackStack;
        private boolean fade;
        private boolean removeLast;
        private boolean disableReuse;
        private int remainCount;

        private SingleChildOperator(@NonNull Fragment hostFragment, Fragment childFragment) {
            this(hostFragment, childFragment, getTag(childFragment));
        }

        @SuppressLint("CommitTransaction")
        private SingleChildOperator(@NonNull Fragment hostFragment, String tag) {
            this(hostFragment, null, tag);
            // retrieve correct childFragment
            List<Fragment> fragments = getChildFragmentList(hostFragment);
            for (Fragment tagFragment : fragments) {
                if (Strings.equals(tagFragment.getTag(), tag)) {
                    this.childFragment = tagFragment;
                    break;
                }
            }
        }

        @SuppressLint("CommitTransaction")
        private SingleChildOperator(@NonNull Fragment hostFragment,
                @Nullable Fragment childFragment, String tag) {
            this.hostFragment = hostFragment;
            this.childFragment = childFragment;
            this.transaction = hostFragment.getChildFragmentManager().beginTransaction();
            this.tag = tag;

            transaction.setAllowOptimization(true);
        }

        public SingleChildOperator bindPresenter(@NonNull BasePresenter presenter) {
            this.presenter = presenter;
            return this;
        }

        /**
         * setArguments to target childFragment.
         */
        public SingleChildOperator data(@NonNull Bundle data) {
            if (childFragment != null) {
                childFragment.setArguments(data);
            } else {
                Logger.t(TAG).e("childFragment is null, will not add data to arguments");
            }
            return this;
        }

        /**
         * simple string bundle as argument
         */
        public SingleChildOperator data(@NonNull String key, @Nullable String value) {
            if (childFragment != null) {
                Bundle bundle = childFragment.getArguments() == null ? new Bundle()
                        : childFragment.getArguments();
                bundle.putString(key, value);
                childFragment.setArguments(bundle);
            } else {
                Logger.t(TAG).e("childFragment is null, will not add data to arguments");
            }
            return this;
        }

        public SingleChildOperator addSharedElement(View sharedElement, String name) {
            transaction.addSharedElement(sharedElement, name);
            return this;
        }

        public SingleChildOperator setCustomAnimator(@AnimRes int enter, @AnimRes int exit) {
            transaction.setCustomAnimations(enter, exit);
            return this;
        }

        public SingleChildOperator setCustomAnimator(@AnimRes int enter, @AnimRes int exit,
                @AnimRes int popEnter, @AnimRes int popExit) {
            transaction.setCustomAnimations(enter, exit, popEnter, popExit);
            return this;
        }

        /**
         * Fragments use transaction optimization for better performance, if you face issues please
         * disable it.
         */
        public SingleChildOperator disableOptimize() {
            transaction.setAllowOptimization(false);
            return this;
        }

        public SingleChildOperator addToBackStack() {
            this.addToBackStack = true;
            return this;
        }

        /**
         * display fade transition
         */
        public SingleChildOperator fade() {
            this.fade = true;
            return this;
        }

        /**
         * remove last fragment while checkout.
         */
        public SingleChildOperator removeLast() {
            return removeLast(0);
        }

        /**
         * remove last fragment while checkout. it can remain a few of fragment for faster
         * recovery
         *
         * @param remain the number that last fragment will remain
         */
        public SingleChildOperator removeLast(int remain) {
            this.removeLast = true;
            this.remainCount = remain;
            return this;
        }

        /**
         * Fragments will reuse exists fragment when fragment tag is the same. Disable it will
         * force to use newly fragment instead of old one.
         */
        public SingleChildOperator disableReuse() {
            this.disableReuse = true;
            return this;
        }

        /**
         * @return success or not
         */

        @SuppressWarnings("unchecked") public boolean into(@IdRes int containerId) {
            if (childFragment == null) {
                Logger.t(TAG).e("childFragment is null, will do nothing");
                commit();
                return false;
            }

            List<Fragment> fragments = getChildFragmentList(hostFragment);
            final String hostTag = getTag(hostFragment);
            final int addedCount = getAddedCount(fragments, containerId);
            boolean canRemove =
                    removeLast && consumeRemainPool(remainCount, hostTag, addedCount) < 0;

            // hide or remove last fragment
            for (Fragment oldFragment : fragments) {
                if (oldFragment == null
                        || oldFragment.getId() != containerId
                        || !oldFragment.isAdded()) {
                    continue;
                }

                if (Strings.equals(oldFragment.getTag(), tag)) {
                    if (!disableReuse) {
                        childFragment = oldFragment; // found previous, use old to keep data
                    } else {
                        Logger.d("reuse disabled, remove");
                        transaction.remove(oldFragment);
                    }
                } else {
                    if (canRemove) {
                        Logger.d(oldFragment + " removed");
                        transaction.remove(oldFragment);
                        canRemove = false;
                    } else if (oldFragment.isVisible()) {
                        Logger.d(oldFragment + " hide");
                        transaction.hide(oldFragment);
                        oldFragment.setUserVisibleHint(false);
                    }
                }
            }

            boolean canAddBackStack =
                    transaction.isAddToBackStackAllowed() && !transaction.isEmpty();

            if (fade) {
                // noinspection WrongConstant
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            }

            if (addToBackStack) {
                if (canAddBackStack) {
                    transaction.addToBackStack(tag);
                } else {
                    Logger.t(TAG).w("addToBackStack called, but this is not permitted");
                }
            }

            if (!childFragment.isAdded()) {
                transaction.add(containerId, childFragment, tag);
            }

            if (presenter != null) {
                presenter.setView(childFragment);
            }

            transaction.show(childFragment);

            commit();
            return true;
        }

        private void commit() {
            transaction.commitAllowingStateLoss();

            if (childFragment != null && childFragment instanceof BaseFragment) {
                ((BaseFragment) childFragment).onVisible();
            }

            transaction = null;
            childFragment = null;
            presenter = null;
            hostFragment = null;
        }
    }

    public static final class SingleOperator {
        private FragmentActivity activity;
        @Nullable private Fragment fragment;
        private BasePresenter presenter;
        private String tag;
        private FragmentTransaction transaction;

        private boolean addToBackStack;
        private boolean fade;
        private boolean removeLast;
        private boolean disableReuse;
        private int remainCount;

        private SingleOperator(@NonNull FragmentActivity activity, Fragment fragment) {
            this(activity, fragment, getTag(fragment));
        }

        @SuppressLint("CommitTransaction")
        private SingleOperator(@NonNull FragmentActivity activity, String tag) {
            this(activity, null, tag);
            // retrieve correct fragment
            List<Fragment> fragments = getFragmentList(activity);
            for (Fragment tagFragment : fragments) {
                if (Strings.equals(tagFragment.getTag(), tag)) {
                    this.fragment = tagFragment;
                    break;
                }
            }
        }

        @SuppressLint("CommitTransaction")
        private SingleOperator(@NonNull FragmentActivity activity, @Nullable Fragment fragment,
                String tag) {
            this.activity = activity;
            this.fragment = fragment;
            this.transaction = activity.getSupportFragmentManager().beginTransaction();
            this.tag = tag;

            transaction.setAllowOptimization(true);
        }

        public SingleOperator bindPresenter(@NonNull BasePresenter presenter) {
            this.presenter = presenter;
            return this;
        }

        /**
         * setArguments to target fragment.
         */
        public SingleOperator data(@NonNull Bundle data) {
            if (fragment != null) {
                fragment.setArguments(data);
            } else {
                Logger.t(TAG).e("fragment is null, will not add data to arguments");
            }
            return this;
        }

        /**
         * simple string bundle as argument
         */
        public SingleOperator data(@NonNull String key, @Nullable String value) {
            if (fragment != null) {
                Bundle bundle =
                        fragment.getArguments() == null ? new Bundle() : fragment.getArguments();
                bundle.putString(key, value);
                fragment.setArguments(bundle);
            } else {
                Logger.t(TAG).e("fragment is null, will not add data to arguments");
            }
            return this;
        }

        public SingleOperator addSharedElement(View sharedElement, String name) {
            transaction.addSharedElement(sharedElement, name);
            return this;
        }

        public SingleOperator setCustomAnimator(@AnimRes int enter, @AnimRes int exit) {
            transaction.setCustomAnimations(enter, exit);
            return this;
        }

        public SingleOperator setCustomAnimator(@AnimRes int enter, @AnimRes int exit,
                @AnimRes int popEnter, @AnimRes int popExit) {
            transaction.setCustomAnimations(enter, exit, popEnter, popExit);
            return this;
        }

        /**
         * Fragments use transaction optimization for better performance, if you face issues please
         * disable it.
         */
        public SingleOperator disableOptimize() {
            transaction.setAllowOptimization(false);
            return this;
        }

        public SingleOperator addToBackStack() {
            this.addToBackStack = true;
            return this;
        }

        /**
         * display fade transition
         */
        public SingleOperator fade() {
            this.fade = true;
            return this;
        }

        /**
         * remove last fragment while checkout.
         */
        public SingleOperator removeLast() {
            return removeLast(0);
        }

        /**
         * remove last fragment while checkout. it can remain a few of fragment for faster
         * recovery
         *
         * @param remain the number that last fragment will remain
         */
        public SingleOperator removeLast(int remain) {
            this.removeLast = true;
            this.remainCount = remain;
            return this;
        }

        /**
         * Fragments will reuse exists fragment when fragment tag is the same. Disable it will
         * force to use newly fragment instead of old one.
         */
        public SingleOperator disableReuse() {
            this.disableReuse = true;
            return this;
        }

        /**
         * @return success or not
         */
        @SuppressWarnings("unchecked") public boolean into(@IdRes int containerId) {
            if (fragment == null) {
                Logger.t(TAG).e("fragment is null, will do nothing");
                commit();
                return false;
            }

            List<Fragment> fragments = getFragmentList(activity);
            final String activityTag =
                    activity instanceof BaseActivity ? ((BaseActivity) activity).getSimpleName()
                            : activity.getLocalClassName();
            final int addedCount = getAddedCount(fragments, containerId);
            boolean canRemove =
                    removeLast && consumeRemainPool(remainCount, activityTag, addedCount) < 0;

            // hide or remove last fragment
            for (Fragment oldFragment : fragments) {
                if (oldFragment == null
                        || oldFragment.getId() != containerId
                        || !oldFragment.isAdded()) {
                    continue;
                }

                if (Strings.equals(oldFragment.getTag(), tag)) {
                    if (!disableReuse) {
                        fragment = oldFragment; // found previous, use old to keep data
                    } else {
                        Logger.d("reuse disabled, remove");
                        transaction.remove(oldFragment);
                    }
                } else {
                    if (canRemove) {
                        Logger.d(oldFragment + " removed");
                        transaction.remove(oldFragment);
                        canRemove = false;
                    } else if (oldFragment.isVisible()) {
                        Logger.d(oldFragment + " hide");
                        transaction.hide(oldFragment);
                        oldFragment.setUserVisibleHint(false);
                    }
                }
            }

            boolean canAddBackStack =
                    transaction.isAddToBackStackAllowed() && !transaction.isEmpty();

            if (fade) {
                // noinspection WrongConstant
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            }

            if (addToBackStack) {
                if (canAddBackStack) {
                    transaction.addToBackStack(tag);
                } else {
                    Logger.t(TAG).w("addToBackStack called, but this is not permitted");
                }
            }

            if (!fragment.isAdded()) {
                transaction.add(containerId, fragment, tag);
            }

            if (presenter != null) {
                presenter.setView(fragment);
            }

            transaction.show(fragment);

            commit();
            return true;
        }

        private void commit() {
            transaction.commitAllowingStateLoss();

            if (fragment != null && fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onVisible();
            }

            transaction = null;
            fragment = null;
            presenter = null;
            activity = null;
        }
    }

    // TODO: 6/10/16 MultiOperator is not used that much, so I only give it basic into function here.
    public static final class MultiOperator {
        private Fragment[] fragments;
        private WeakReference<FragmentActivity> activityRef;

        @SuppressLint("CommitTransaction")
        public MultiOperator(FragmentActivity activity, Fragment[] fragments) {
            this.fragments = fragments;
            activityRef = new WeakReference<>(activity);
        }

        public void into(int... ids) {
            if (ids.length != fragments.length) {
                throw new IllegalArgumentException("The length of ids and fragments is not equal.");
            }

            FragmentTransaction transaction =
                    activityRef.get().getSupportFragmentManager().beginTransaction();
            String tag;
            for (int i = 0, s = ids.length; i < s; i++) {
                tag = getTag(fragments[i]);
                transaction.replace(ids[i], fragments[i], tag);
            }
            transaction.commitAllowingStateLoss();

            activityRef.clear();
            fragments = null;
        }
    }
}
