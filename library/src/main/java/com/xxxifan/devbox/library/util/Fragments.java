/*
 * Copyright  (c) 2016 xxxifan
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

package com.xxxifan.devbox.library.util;

import android.annotation.SuppressLint;
import android.support.annotation.AnimRes;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.xxxifan.devbox.library.base.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by xifan on 6/7/16.
 */
public class Fragments {
    public static final String TAG = "Fragments";
    public static final String KEY_RESTORE = "restore";
    public static final String KEY_RESTORE_VIEWPAGER = "restore_viewpager";

    private Fragments() {
    }

    /**
     * checkout with FRAGMENT_CONTAINER(which is defined in BaseActivity, is R.id.fragment_container
     * it will use BaseFragment.getSimpleName() as tag, or SimpleClassName if fallback.
     */
    @CheckResult
    public static SingleOperator checkout(FragmentActivity activity, Fragment fragment) {
        return new SingleOperator(activity, fragment);
    }

    /**
     * checkout with specified tag
     */
    @CheckResult
    public static SingleOperator checkout(FragmentActivity activity, Fragment fragment, String tag) {
        return new SingleOperator(activity, fragment, tag);
    }

    /**
     * checkout previously fragment by tag
     */
    @CheckResult
    public static SingleOperator checkout(FragmentActivity activity, String tag) {
        return new SingleOperator(activity, tag);
    }

    /**
     * add multi fragments
     */
    @CheckResult
    public static MultiOperator add(FragmentActivity activity, Fragment... fragments) {
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

    public static List<Fragment> getFragmentList(FragmentActivity activity) {
        return activity.getSupportFragmentManager().getFragments();
    }

    private static String getTag(Fragment fragment) {
        return Strings.isEmpty(fragment.getTag())
                ? (fragment instanceof BaseFragment ? ((BaseFragment) fragment).getSimpleName() : fragment
                .getClass().getName()) : fragment.getTag();
    }

    public static class SingleOperator {
        private WeakReference<FragmentActivity> activityRef;
        private Fragment fragment;
        private String tag;
        private FragmentTransaction transaction;

        private boolean addToBackStack;
        private boolean fade;
        private boolean removeLast;
        private boolean hideLast = true;

        private SingleOperator(FragmentActivity activity, Fragment fragment) {
            this(activity, fragment, getTag(fragment));
        }

        @SuppressLint("CommitTransaction")
        private SingleOperator(FragmentActivity activity, Fragment fragment, String tag) {
            this.activityRef = new WeakReference<>(activity);
            this.fragment = fragment;
            this.tag = tag;
            this.transaction = activity.getSupportFragmentManager().beginTransaction();
        }

        @SuppressLint("CommitTransaction")
        private SingleOperator(FragmentActivity activity, String tag) {
            this.activityRef = new WeakReference<>(activity);
            this.tag = tag;
            this.transaction = activity.getSupportFragmentManager().beginTransaction();

            // retrieve correct fragment
            List<Fragment> fragments = getFragmentList(activity);
            for (Fragment tagFragment : fragments) {
                if (Strings.equals(tagFragment.getTag(), tag)) {
                    this.fragment = tagFragment;
                    break;
                }
            }
        }

        public SingleOperator addSharedElement(View sharedElement, String name) {
            transaction.addSharedElement(sharedElement, name);
            return this;
        }

        public SingleOperator setCustomAnimator(@AnimRes int enter, @AnimRes int exit) {
            transaction.setCustomAnimations(enter, exit);
            return this;
        }

        public SingleOperator setCustomAnimator(@AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit) {
            transaction.setCustomAnimations(enter, exit, popEnter, popExit);
            return this;
        }

        public SingleOperator addToBackStack(boolean add) {
            this.addToBackStack = add;
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
         * hideLast last fragment, default is true.
         * if you want last to remove, see {@link #removeLast(boolean)}
         */
        public SingleOperator hideLast(boolean hideLast) {
            this.hideLast = hideLast;
            return this;
        }

        /**
         * remove last fragment while checkout.
         */
        public SingleOperator removeLast(boolean remove) {
            this.removeLast = remove;
            return this;
        }

        public void into(@IdRes int containerId) {
            if (fragment == null) {
                Logger.t(TAG).e("fragment is null, will not do anything");
                finish();
                return;
            }

            // hide or remove last fragment
            if (hideLast || removeLast) {
                List<Fragment> fragments = getFragmentList(activityRef.get());
                if (fragments != null) {
                    for (Fragment oldFragment : fragments) {
                        if (oldFragment == null) {
                            continue;
                        }

                        if (oldFragment.getId() == containerId) {
                            if (Strings.equals(oldFragment.getTag(), tag)) {
                                fragment = oldFragment;
                            } else if (oldFragment.isVisible()) {
                                oldFragment.setUserVisibleHint(false);
                                transaction.hide(oldFragment);
                                if (removeLast) {
                                    Logger.d("last fragment has been totally removed");
                                    transaction.remove(oldFragment);
                                }
                            }
                        }
                    }
                }
            }

            boolean canAddBackStack = transaction.isAddToBackStackAllowed() && !transaction.isEmpty();

            if (fade) {
                // noinspection WrongConstant
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            }

            if (!fragment.isAdded()) {
                transaction.add(containerId, fragment, tag);
            }

            transaction.show(fragment);

            if (addToBackStack) {
                if (canAddBackStack) {
                    transaction.addToBackStack(tag);
                } else {
                    Logger.t(TAG)
                            .w("addToBackStack called, but this is not permitted");
                }
            }

            finish();
        }

        private void finish() {
            transaction.commitAllowingStateLoss();
            transaction = null;
            fragment = null;
            activityRef.clear();
        }
    }

    // TODO: 6/10/16 MultiOperator is not used that much, so I only give it basic into function here.
    public static class MultiOperator {
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

            FragmentTransaction transaction = activityRef.get()
                    .getSupportFragmentManager()
                    .beginTransaction();
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
