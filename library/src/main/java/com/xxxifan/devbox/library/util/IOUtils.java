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

package com.xxxifan.devbox.library.util;

import com.xxxifan.devbox.library.Devbox;

import java.io.File;
import java.io.IOException;

import okio.BufferedSource;
import okio.Okio;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xifan on 3/30/16.
 */
public class IOUtils {

    public static File getCacheDir() {
        File dir = Devbox.getAppDelegate().getExternalCacheDir();
        if (dir == null) {
            dir = Devbox.getAppDelegate().getCacheDir();
        }
        return dir;
    }

    public static void runCmd(String[] cmd, CommandCallback callback) {
        Process p;
        String result;
        try {
            p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            BufferedSource source = Okio.buffer(Okio.source(p.getInputStream()));
            result = source.readUtf8().trim();
            if (callback != null) {
                callback.done(result, null);
            }
            p.destroy();
            source.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.done(null, e);
            }
        }
    }

    public static <T> Observable.Transformer<T, T> io() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Observable.Transformer<T, T> computation() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public interface CommandCallback {
        void done(String result, IOException e);
    }
}
