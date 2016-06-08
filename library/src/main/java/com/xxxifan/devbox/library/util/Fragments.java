package com.xxxifan.devbox.library.util;

import android.annotation.SuppressLint;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.xxxifan.devbox.library.base.BaseFragment;
import com.xxxifan.devbox.library.util.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by xifan on 6/7/16.
 */
public class Fragments {
    public static final String TAG = "Fragments";

    private static Fragment sLastFragment;

    private Fragments() {
    }
    // TODO: 6/8/16 new method to prepare a list for no-create checkout

    public static Operator checkout(FragmentActivity activity, Fragment fragment) {
        return new Operator(activity, fragment);
    }

    public static Operator checkout(FragmentActivity activity, Fragment fragment, String tag) {
        return new Operator(activity, fragment, tag);
    }

    public static Operator checkout(FragmentActivity activity, String tag) {
        return new Operator(activity, tag);
    }

    public static class Operator {
        private WeakReference<FragmentActivity> activityRef;
        private Fragment fragment;
        private String tag;
        private FragmentManager fragmentManager;
        private FragmentTransaction transaction;

        private boolean detachLast;
        private boolean addToBackStack;
        private boolean noHide;
        private boolean fade;

        private Operator(FragmentActivity activity, Fragment fragment) {
            this(activity, fragment, StringUtils.isEmpty(fragment.getTag()) ? (fragment instanceof BaseFragment ? ((BaseFragment) fragment).getSimpleName() : fragment.getClass().getName()) : fragment.getTag());
        }

        @SuppressLint("CommitTransaction")
        private Operator(FragmentActivity activity, Fragment fragment, String tag) {
            this.activityRef = new WeakReference<>(activity);
            this.fragment = fragment;
            this.tag = tag;
            this.fragmentManager = activity.getSupportFragmentManager();
            this.transaction = fragmentManager.beginTransaction();
        }

        @SuppressLint("CommitTransaction")
        private Operator(FragmentActivity activity, String tag) {
            this.activityRef = new WeakReference<>(activity);
            this.tag = tag;
            this.fragmentManager = activity.getSupportFragmentManager();
            this.transaction = fragmentManager.beginTransaction();

            // retrieve correct fragment
            for (Fragment tagFragment : fragmentManager.getFragments()) {
                if (StringUtils.equals(tagFragment.getTag(), tag)) {
                    this.fragment = tagFragment;
                    break;
                }
            }
        }

        public Operator addSharedElement(View sharedElement, String name) {
            transaction.addSharedElement(sharedElement, name);
            return this;
        }

        public Operator setCustomAnimator(@AnimRes int enter, @AnimRes int exit) {
            transaction.setCustomAnimations(enter, exit);
            return this;
        }

        public Operator setCustomAnimator(@AnimRes int enter, @AnimRes int exit, @AnimRes int popEnter, @AnimRes int popExit) {
            transaction.setCustomAnimations(enter, exit, popEnter, popExit);
            return this;
        }

        public Operator detachLast(boolean detach) {
            this.detachLast = detach;
            return this;
        }

        public Operator addToBackStack(boolean add) {
            this.addToBackStack = add;
            return this;
        }

        public Operator fade() {
            this.fade = true;
            return this;
        }

        public Operator noHide() {
            this.noHide = true;
            return this;
        }

        public void into(@IdRes int containerId) {
            if (fragment == null) {
                Logger.t(TAG).e("fragment is null, will not do anything");
                return;
            }

            // hide other fragment if need
            if (!noHide) {
                List<Fragment> fragments = fragmentManager.getFragments();
                if (fragments != null) {
                    for (Fragment oldFragment : fragments) {
                        if (oldFragment == null) {
                            continue;
                        }

                        if (StringUtils.equals(oldFragment.getTag(), tag)) {
                            Logger.d("same tag fragment found!");
                            oldFragment.setUserVisibleHint(false);
                            transaction.remove(oldFragment).detach(oldFragment);
                        } else if (oldFragment.isVisible()) {
                            oldFragment.setUserVisibleHint(false);
                            transaction.hide(oldFragment);
                            // TODO: 6/8/16 get correct last fragment
                            if (oldFragment == sLastFragment && detachLast) {
                                transaction.detach(oldFragment);
                            }
                        }
                    }
                }

                sLastFragment = fragment;
            }

            if (fade) {
                // noinspection WrongConstant
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            } else {
                // TODO: 6/8/16 custom animation
            }

            if (!fragment.isAdded()) {
                transaction.add(containerId, fragment, tag);
            }

            transaction.show(fragment);

            if (addToBackStack) {
                // FIXME: 6/8/16 fragment already added
                transaction.addToBackStack(tag);
            }

            transaction.commitAllowingStateLoss();

            // manually call setUserVisibleHint to notify it'll be visible soon.
            fragment.setUserVisibleHint(true);

            // clear
            fragment = null;
            fragmentManager = null;
            activityRef.clear();
        }
    }
}
