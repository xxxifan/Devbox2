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

package com.xxxifan.devbox.library.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;

import com.orhanobut.logger.Logger;
import com.xxxifan.devbox.library.Devbox;
import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.event.NetworkEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by xifan on 6/22/16.
 */
public class DataLoader {
    private static final String PAGE_STATE = "page";
    private static final String LOADING_STATE = "isLoading";
    private static final String DATA_LOAD_STATE = "dataLoaded";
    private static final String DATA_END_STATE = "dataEnd";
    private static final String LAZY_LOAD_STATE = "lazyLoad";
    private static final String NETWORK_STATE = "useNetwork";

    private LoadCallback callback;

    private boolean isDataLoaded;
    private boolean isDataEnd;
    private boolean isLazyLoadEnabled;
    private boolean useNetwork;
    private AtomicBoolean isLoading;

    private int mPage;

    private DataLoader(LoadCallback callback) {
        isLoading = new AtomicBoolean(false);
        this.callback = callback;
    }

    public static DataLoader init(boolean useNetwork, LoadCallback callbacks) {
        DataLoader dataLoadManager = new DataLoader(callbacks);
        dataLoadManager.resetPage(); // init page
        dataLoadManager.useNetwork(useNetwork);
        return dataLoadManager;
    }

    private static boolean hasNetwork() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) Devbox
                .getAppDelegate()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    // ########## Load Process ##########

    public void startRefresh() {
        if (isLoading.get()) {
            Logger.t(toString()).d("load is in progress, dismiss");
            return;
        } else {
            isLoading.set(true);
        }
        if (callback == null) {
            Logger.t(toString()).d("load callback is null");
            isLoading.set(false); // reset state
            return;
        }
        if (useNetwork && !hasNetwork()) {
            NetworkEvent event = new NetworkEvent(
                    Devbox.getAppDelegate().getString(R.string.msg_network_unavailable));
            EventBus.getDefault().post(event);
            isLoading.set(false); // reset state
            Logger.t(toString()).d("network not available, dismiss");
            return;
        }

        // ready to start
        if (callback instanceof ListLoadCallback) {
            ((ListLoadCallback) callback).onRefreshStart();
        } else {
            isLoading.set(false); // reset state
            startLoad();
        }
    }

    public void startLoad() {
        onDataLoad(false);
    }

    public void startLazyLoad() {
        onDataLoad(true);
    }

    private void onDataLoad(boolean lazyMode) {
        if (!isLoading.get()) {
            isLoading.set(true);
        } else {
            Logger.t(toString()).d("load is in progress, dismiss");
            return;
        }
        if (callback == null) {
            Logger.t(toString()).d("load callback is null");
            isLoading.set(false); // reset state
            return;
        }

        boolean lazyLoad = isLazyLoadEnabled() && lazyMode;
        boolean normalLoad = !isLazyLoadEnabled() && !lazyMode;

        if (!isDataLoaded() && !isDataEnd() && (lazyLoad || normalLoad)) {
            if (useNetwork && !hasNetwork()) {
                NetworkEvent event = new NetworkEvent(
                        Devbox.getAppDelegate().getString(R.string.msg_network_unavailable));
                EventBus.getDefault().post(event);
                isLoading.set(false); // reset state
                Logger.t(toString()).d("network not available, dismiss");
                return;
            }

            boolean isDataLoaded = callback.onLoadStart();
            setDataLoaded(isDataLoaded);
            // reset lazy load, it will only call once
            isLazyLoadEnabled = false;
        } else {
            isLoading.set(false);
        }
    }

    // ############ Flags ############

    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    // mark data requested, then it won't call startLoad() again in onResume().
    public void setDataLoaded(boolean loaded) {
        isDataLoaded = loaded;
    }

    public boolean isDataEnd() {
        return isDataEnd;
    }

    /**
     * mark data is on the bottom, no more refresh should be requested.
     */
    public void setDataEnd(boolean end) {
        isDataEnd = end;
    }

    public boolean isLazyLoadEnabled() {
        return isLazyLoadEnabled;
    }

    /**
     * better be used with fragments in ViewPager
     */
    public void enableLazyLoad() {
        this.isLazyLoadEnabled = true;
    }

    public boolean isLoading() {
        return isLoading.get();
    }

    public void useNetwork(boolean useNetwork) {
        this.useNetwork = useNetwork;
    }

    // ########### Functions ###########

    public void notifyPageLoaded() {
        isLoading.set(false);
        if (callback != null && callback instanceof ListLoadCallback) {
            mPage++;
            if (Looper.myLooper() != Looper.getMainLooper()) {
                Observable.just(null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Object>() {
                            @Override public void call(Object o) {
                                ((ListLoadCallback) callback).notifyDataLoaded();
                            }
                        }, new Action1<Throwable>() {
                            @Override public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
            } else {
                ((ListLoadCallback) callback).notifyDataLoaded();
            }
        }
        setDataLoaded(true);
    }

    public void notifyPageLoadFailed() {
        isLoading.set(false);
        setDataLoaded(false);
    }

    public int getPage() {
        return mPage;
    }

    public void resetPage() {
        mPage = 1;
    }

    public void destroy() {
        callback = null;
    }

    public void onSavedState(Bundle savedInstanceState) {
        savedInstanceState.putInt(PAGE_STATE, mPage);
        savedInstanceState.putBoolean(LOADING_STATE, isLoading());
        savedInstanceState.putBoolean(DATA_LOAD_STATE, isDataLoaded);
        savedInstanceState.putBoolean(DATA_END_STATE, isDataEnd);
        savedInstanceState.putBoolean(LAZY_LOAD_STATE, isLazyLoadEnabled);
        savedInstanceState.putBoolean(NETWORK_STATE, useNetwork);
    }

    public void onRestoreState(Bundle savedInstanceState) {
        mPage = savedInstanceState.getInt(PAGE_STATE);
        isLoading.set(savedInstanceState.getBoolean(LOADING_STATE));
        savedInstanceState.putBoolean(DATA_LOAD_STATE, isDataLoaded);
        savedInstanceState.putBoolean(DATA_END_STATE, isDataEnd);
        savedInstanceState.putBoolean(LAZY_LOAD_STATE, isLazyLoadEnabled);
        savedInstanceState.putBoolean(NETWORK_STATE, useNetwork);
    }

    public interface LoadCallback {
        /**
         * load data in this callback, should be called on setUserVisibleHint() at first time and later on onResume()
         *
         * @return true if data load finished, which means it won't load data again while visible lifecycle.
         * If you want it handled by load task, leave it false.
         */
        boolean onLoadStart();
    }

    public interface ListLoadCallback extends LoadCallback {
        /**
         * setRefresh data list due to loadType, should be called in onLoadStart().
         */
        void onRefreshStart();

        void notifyDataLoaded();
    }

    public static class RxNotifier {
        private WeakReference<DataLoader> mDataLoaderRef;

        private RxNotifier(DataLoader dataLoader) {
            mDataLoaderRef = new WeakReference<>(dataLoader);
        }

        /**
         * It's work in main thread, make sure it will be called after scheduler transformer
         */
        public static <T> Observable.Transformer<T, T> instance(DataLoader dataLoader) {
            final RxNotifier notifier = new RxNotifier(dataLoader);
            return new Observable.Transformer<T, T>() {
                @Override
                public Observable<T> call(Observable<T> observable) {
                    return observable
                            .doOnCompleted(new Action0() {
                                @Override public void call() {
                                    if (notifier.getDataLoader() != null) {
                                        notifier.getDataLoader().notifyPageLoaded();
                                        // clear ref
                                        notifier.getDataLoaderRef().clear();
                                    }
                                }
                            })
                            .doOnError(new Action1<Throwable>() {
                                @Override public void call(Throwable throwable) {
                                    if (notifier.getDataLoader() != null) {
                                        notifier.getDataLoader().notifyPageLoadFailed();
                                        // clear ref
                                        notifier.getDataLoaderRef().clear();
                                    }
                                }
                            });
                }
            };
        }

        private DataLoader getDataLoader() {
            return mDataLoaderRef != null ? mDataLoaderRef.get() : null;
        }

        private WeakReference<DataLoader> getDataLoaderRef() {
            return mDataLoaderRef;
        }

    }
}
