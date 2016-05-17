package com.xxxifan.devbox.library.util;

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
