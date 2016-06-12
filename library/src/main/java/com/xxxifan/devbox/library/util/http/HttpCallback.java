package com.xxxifan.devbox.library.util.http;

import com.xxxifan.devbox.library.util.ReflectUtils;

import java.lang.reflect.Type;

/**
 * Created by xifan on 6/12/16.
 */
public abstract class HttpCallback<T> {
    private Type type;
    public HttpCallback() {
        type = ReflectUtils.getGenericType(getClass());
    }

    public Type getGenericType() {
        return type;
    }

    public abstract void onSuccess(T result);

    public abstract void onFailed(Throwable throwable);

}
