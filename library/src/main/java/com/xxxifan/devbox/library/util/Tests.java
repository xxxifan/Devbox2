package com.xxxifan.devbox.library.util;

/**
 * Created by xifan on 6/8/16.
 */
public class Tests {
    public static <T> T checkNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static boolean checkBoolean(boolean aBoolean) {
        if (!aBoolean) {
            throw new IllegalArgumentException();
        }
        return aBoolean;
    }
}
