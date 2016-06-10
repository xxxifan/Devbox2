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

    private Fragments() {
    }
    // TODO: 6/8/16 new method to prepare a list for no-create checkout

    public static SingleOperator checkout(FragmentActivity activity, Fragment fragment) {
        return new SingleOperator(activity, fragment);
    }

    public static SingleOperator checkout(FragmentActivity activity, Fragment fragment, String tag) {
        return new SingleOperator(activity, fragment, tag);
    }

    public static SingleOperator checkout(FragmentActivity activity, String tag) {
        return new SingleOperator(activity, tag);
    }

    public static MultiOperator add() {
        return new MultiOperator();
    }

    /**
     * get last visible fragment
     */
    public static Fragment getLastFragment(FragmentActivity activity) {
        Fragment lastDisplayFragment = null;
        if (activity != null) {
            List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null && fragment.isVisible()) {
                        lastDisplayFragment = fragment;
                    }
                }
            }
        }
        return lastDisplayFragment;
    }

    public static class SingleOperator {
        private WeakReference<FragmentActivity> activityRef;
        private Fragment fragment;
        private String tag;
        private FragmentManager fragmentManager;
        private FragmentTransaction transaction;

        private boolean addToBackStack;
        private boolean fade;
        private boolean removeLast;
        private boolean replaceLast = true;

        private SingleOperator(FragmentActivity activity, Fragment fragment) {
            this(activity, fragment, StringUtils.isEmpty(fragment.getTag()) ? (fragment instanceof BaseFragment ? ((BaseFragment) fragment).getSimpleName() : fragment.getClass().getName()) : fragment.getTag());
        }

        @SuppressLint("CommitTransaction")
        private SingleOperator(FragmentActivity activity, Fragment fragment, String tag) {
            this.activityRef = new WeakReference<>(activity);
            this.fragment = fragment;
            this.tag = tag;
            this.fragmentManager = activity.getSupportFragmentManager();
            this.transaction = fragmentManager.beginTransaction();
        }

        @SuppressLint("CommitTransaction")
        private SingleOperator(FragmentActivity activity, String tag) {
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

        /**
         * remove last fragment while checkout.
         */
        public SingleOperator removeLast(boolean remove) {
            this.removeLast = remove;
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
         * replace last fragment, default is true.
         * if you want last to remove, see {@link #removeLast(boolean)}
         */
        public SingleOperator replaceLast(boolean replace) {
            this.replaceLast = replace;
            return this;
        }

        public void into(@IdRes int containerId) {
            if (fragment == null) {
                Logger.t(TAG).e("fragment is null, will not do anything");
                return;
            }

            // hide or remove last fragment
            if (replaceLast || removeLast) {
                List<Fragment> fragments = fragmentManager.getFragments();
                if (fragments != null) {
                    Fragment lastFragment = null;
                    for (Fragment oldFragment : fragments) {
                        if (oldFragment == null) {
                            continue;
                        }

                        if (StringUtils.equals(oldFragment.getTag(), tag)) {
                            Logger.d("same tag fragment found!");
                            // use old fragment
                            fragment = oldFragment;
                        } else if (oldFragment.isVisible()) {
                            oldFragment.setUserVisibleHint(false);
                            transaction.hide(oldFragment);
                            if (lastFragment == null) {
                                lastFragment = getLastFragment(activityRef.get());
                            }
                            if (StringUtils.equals(lastFragment.getTag(), oldFragment.getTag())  && removeLast) {
                                Logger.d("last fragment has been totally removed");
                                transaction.remove(oldFragment);
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

            transaction.commitAllowingStateLoss();

            // manually call setUserVisibleHint to notify it'll be visible soon.
            fragment.setUserVisibleHint(true);

            // clear
            fragment = null;
            fragmentManager = null;
            activityRef.clear();
        }
    }

    public static class MultiOperator {

    }
}
