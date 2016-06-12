package com.xxxifan.devbox.library.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * Very simple wrapper for image loaders.
 * For various image loader functions, it's a easier way to return its instance instead function wrappers.
 * Created by xifan on 6/12/16.
 */
public class ImageLoader {
    public static RequestManager glide(Context context) {
        return Glide.with(context);
    }
}
