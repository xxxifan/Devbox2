package com.xxxifan.devbox.demo;

import android.app.Application;
import android.os.StrictMode;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.xxxifan.devbox.components.http.Http;
import com.xxxifan.devbox.core.Devbox;
import com.xxxifan.devbox.core.util.IOUtils;

import java.util.concurrent.TimeUnit;

import cn.dreamtobe.filedownloader.OkHttp3Connection;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

    @Override public void onCreate() {
        super.onCreate();
        sApp = this;
        Devbox.init(this);

        initHttpComponent();
        Logger.addLogAdapter(new AndroidLogAdapter());
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder()).detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy((new android.os.StrictMode.VmPolicy.Builder()).detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    private void initHttpComponent() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Cache cache = new Cache(IOUtils.getCacheDir(), 300 * 1024 * 1024); // 300MB
        OkHttpClient client =
                new OkHttpClient.Builder().cache(cache).addInterceptor(logging).build();
        Http.initClient(client);

        Retrofit retrofit = new Retrofit.Builder().client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        Http.initRetrofit(retrofit);

        // Init the FileDownloader with the OkHttp3Connection.Creator.
        // use another client to avoid some issues
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20000, TimeUnit.SECONDS); // 20s
        FileDownloader.init(this,
                new DownloadMgrInitialParams.InitCustomMaker().connectionCreator(new OkHttp3Connection.Creator(
                        builder))
        );
    }
}
