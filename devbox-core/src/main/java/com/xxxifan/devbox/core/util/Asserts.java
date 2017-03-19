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

import android.support.annotation.Nullable;

/**
 * 一个奇怪的断言工具。
 * 或许是因为在lambda中写null判断时能写起来更方便才写的 (滑稽
 * Created by xifan on 6/8/16.
 */
public class Asserts {

    private Asserts() {}

    public static <T> T throwNull(@Nullable T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static boolean isNull(@Nullable Object object) {
        return object == null;
    }

    public static boolean notNull(@Nullable Object object) {
        return object != null;
    }

    public static boolean throwTrue(boolean bool) {
        if (!bool) {
            throw new IllegalArgumentException();
        }
        return true;
    }
}
