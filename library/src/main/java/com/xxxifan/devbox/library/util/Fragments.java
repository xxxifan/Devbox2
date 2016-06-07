package com.xxxifan.devbox.library.util;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;

/**
 * Created by xifan on 6/7/16.
 */
public class Fragments {

    /**
     * set fragment will be attach to this activity right now
     */
    public static void setContainerFragment(Fragment fragment) {
        setContainerFragment(fragment, getConfig().getContainerId(), false);
    }

    /**
     * set fragment will be attach to this activity right now
     *
     * @param detach if true will detach other fragment instead of hide.
     */
    public static void setContainerFragment(Fragment fragment, boolean detach) {
        setContainerFragment(fragment, getConfig().getContainerId(), detach);
    }

    /**
     * set fragment will be attach to this activity right now
     *
     * @param containerId the target containerId will be attached to.
     */
    public static void setContainerFragment(Fragment fragment, @IdRes int containerId) {
        setContainerFragment(fragment, containerId, false);
    }


    public static void checkoutFragment(Fragment fragment, @IdRes int containerId, boolean addToBackStack) {

    }

    public static void checkoutFragment(Fragment fragment, @IdRes int containerId, boolean detach, boolean addToBackStack) {

    }
}
