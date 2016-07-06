package com.xxxifan.devbox.demo;

import android.app.Application;
import android.os.StrictMode;

import com.xxxifan.devbox.library.Devbox;
import com.xxxifan.devbox.library.util.IOUtils;
import com.xxxifan.devbox.library.util.http.Http;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xifan on 6/18/16.
 */
public class App extends Application {
    public static final String BASE_URL = "https://api.github.com/";

    private static App sApp;

    public static App get() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        Devbox.init(this);

        Cache cache = new Cache(IOUtils.getCacheDir(), 300 * 1024 * 1024); // 500MB
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        Http.initClient(client);

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Http.initRetrofit(retrofit);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder()).detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy((new android.os.StrictMode.VmPolicy.Builder()).detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
    }
}
