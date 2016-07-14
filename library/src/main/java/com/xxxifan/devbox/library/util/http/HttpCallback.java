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
