/*
 * Copyright  (c) 2016 xxxifan
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

import com.google.gson.Gson;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.Logger;
import com.xxxifan.devbox.library.Devbox;
import com.xxxifan.devbox.library.util.IOUtils;
import com.xxxifan.devbox.library.util.Tests;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * A Simple Http helper class
 * Created by xifan on 6/12/16.
 */
public class Http {
    private static OkHttpClient sClient;
    private static Retrofit sRetrofit;
    private static Gson sGson;

    public static void initClient(OkHttpClient client) {
        sClient = client;
    }

    public static void initRetrofit(Retrofit retrofit) {
        sRetrofit = retrofit;
    }

    public static <T> void send(Request request, final HttpCallback<T> callback) {
        getClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response == null || response.body() == null) {
                            return;
                        }

                        String result = response.body().string();
                        Observable.just(result)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String observableResult) {
                                        if (callback.getGenericType() == String.class) {
                                            callback.onSuccess((T) observableResult);
                                        } else if (callback.getGenericType() == JSONObject.class) {
                                            callback.onSuccess((T) sGson.fromJson(observableResult, JSONObject.class));
                                        } else if (callback.getGenericType() == JSONArray.class) {
                                            callback.onSuccess((T) sGson.fromJson(observableResult, JSONArray.class));
                                        } else {
                                            callback.onSuccess((T) sGson.fromJson(observableResult, callback.getGenericType()));
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailed(e);
                    }
                });
    }

    /**
     * @return download id, can use it for task control later
     */
    public static int download(String url, String path, FileDownloadListener listener) {
        FileDownloader.init(Devbox.getAppDelegate());
        int id = FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(listener)
                .start();
        return id;
    }

    public static void pauseDownlaod(int id) {
        FileDownloader.getImpl().pause(id);
    }

    public static FileDownloader getDownloader() {
        return FileDownloader.getImpl();
    }

    public static void upload(String url, MediaType mediaType, File file, UploadRequestBody.ProgressListener listener) {
        UploadRequestBody requestBody = new UploadRequestBody(
                file,
                mediaType.type(),
                listener
        );

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public static <T> T createRetroService(Class<T> clazz) {
        Tests.checkNull(sRetrofit);
        return sRetrofit.create(clazz);
    }

    public static OkHttpClient getClient() {
        if (sClient == null) {
            synchronized (Http.class) {
                Cache cache = new Cache(IOUtils.getCacheDir(), 500 * 1024 * 1024); // 500MB
                sClient = new OkHttpClient.Builder()
                        .cache(cache)
//                        .socketFactory(new RestrictedSocketFactory(16 * 1024))
                        .build();
                sGson = new Gson();
            }
            Logger.e("You may have not initialized client, it is suggested to init http client via Devbox.setHttpClient()");
        }
        return sClient;
    }

}
