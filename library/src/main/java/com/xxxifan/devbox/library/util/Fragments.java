package com.xxxifan.devbox.library.util;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by xifan on 6/7/16.
 */
public class Fragments {
    // TODO: 6/8/16 new method to prepare a list for no-create checkout

    public static Operator checkout(FragmentActivity activity, Fragment fragment) {
        return new Operator(activity, fragment);
    }

    public static Operator checkout(FragmentActivity activity, Fragment fragment, String tag) {
        return new Operator(activity, fragment, tag);
    }

    public static class Operator {
        private WeakReference<FragmentActivity> activityRef;
        private Fragment fragment;
        private String tag;
        private FragmentManager fragmentManager;
        private FragmentTransaction transaction;

        private boolean detachLast;
        private boolean addToBackStack;

        public Operator(FragmentActivity activity, Fragment fragment) {
            this(activity, fragment, fragment.getTag());
        }

        public Operator(FragmentActivity activity, Fragment fragment, String tag) {
            this.activityRef = new WeakReference<>(activity);
            this.fragment = fragment;
            this.tag = tag;
            this.fragmentManager = activity.getSupportFragmentManager();
            this.transaction = fragmentManager.beginTransaction();
        }

        public Operator addSharedElement(View sharedElement, String name) {
            transaction.addSharedElement(sharedElement, name);
            return this;
        }

        public Operator detachLast(boolean detach) {
            this.detachLast = detachLast;
            return this;
        }

        public Operator addToBackStack(boolean add) {
            this.addToBackStack = add;
            return this;
        }

        public void into(@IdRes int containerId) {
            this.transaction.commit();
        }
    }
}
