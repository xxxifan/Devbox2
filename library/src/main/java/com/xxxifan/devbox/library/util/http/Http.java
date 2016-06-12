package com.xxxifan.devbox.library.util.http;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A Simple Http helper class
 * Created by xifan on 6/12/16.
 */
public class Http {
    public static OkHttpClient sClient;
    private static Gson sGson;

    public static void initClient(OkHttpClient client) {
        sClient = client;
    }

    public static <T> void get(Request request, final HttpCallback<T> callback) {
        getClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response == null || response.body() == null) {
                            return;
                        }

                        String result = response.body().string();
                        if (callback.getGenericType() == String.class) {
                            callback.onSuccess((T) result);
                        } else if (callback.getGenericType() == JSONObject.class) {
                            callback.onSuccess((T) sGson.fromJson(result, JSONObject.class));
                        } else if (callback.getGenericType() == JSONArray.class) {
                            callback.onSuccess((T) sGson.fromJson(result, JSONArray.class));
                        } else {
                            callback.onSuccess((T) sGson.fromJson(result, callback.getGenericType()));
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailed(e);
                    }
                });
    }

    public static void post() {

    }

    public static void download() {

    }

    public static void upload() {

    }

    private static OkHttpClient getClient() {
        if (sClient == null) {
            synchronized (Http.class) {
//                Cache cache = new Cache(IOUtils.getCacheDir(), 500 * 1024 * 1024); // 500MB
                sClient = new OkHttpClient.Builder()
//                        .cache(cache)
                        .build();
                sGson = new Gson();
            }
        }
        return sClient;
    }
}
