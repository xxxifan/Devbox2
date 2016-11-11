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

import android.app.Activity;
import android.content.Context;

import java.util.HashMap;

/**
 * Created by xifan on 7/20/16.
 */
public class StatisticalUtil {

    private static Statistician sStatistician;

    protected StatisticalUtil() {}

    public static void install(Statistician statistician) {
        sStatistician = statistician;
    }

    public static void onPageStart(Activity activity, String tag) {
        if (sStatistician != null) {
            sStatistician.onPageStart(activity, tag);
        }
    }

    public static void onPageStart(Context context, String tag) {
        if (sStatistician != null) {
            sStatistician.onPageStart(context, tag);
        }
    }

    public static void onPageEnd() {
        if (sStatistician != null) {
            sStatistician.onPageEnd();
        }
    }

    public static void onEvent(String name, HashMap<String, Object> data) {
        if (sStatistician != null) {
            sStatistician.onEvent(name, data);
        }
    }

    public interface Statistician {
        void onPageStart(Activity activity, String tag);

        void onPageStart(Context context, String tag);

        void onPageEnd();

        void onEvent(String name, HashMap<String, Object> data);
    }
}
