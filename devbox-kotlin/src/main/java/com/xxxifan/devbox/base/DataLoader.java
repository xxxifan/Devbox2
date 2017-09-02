package com.xxxifan.devbox.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.xxxifan.devbox.Devbox;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by xifan on 6/22/16.
 */
public class DataLoader {
    private static final String PAGE_STATE = "page";
    private static final String LOADING_STATE = "isLoading";
    private static final String REFRESHING_STATE = "isRefreshing";
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
    private AtomicBoolean isRefreshing;

    private int mPage;

    private DataLoader(LoadCallback callback) {
        isLoading = new AtomicBoolean(false);
        isRefreshing = new AtomicBoolean(false);
        this.callback = callback;
    }

    public static DataLoader init(boolean useNetwork, LoadCallback callbacks) {
        DataLoader dataLoadManager = new DataLoader(callbacks);
        dataLoadManager.resetPage(); // init page
        dataLoadManager.useNetwork(useNetwork);
        return dataLoadManager;
    }

    private static boolean hasNetwork() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) Devbox.appDelegate
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    // ########## Load Process ##########

    public void startRefresh() {
        if (isRefreshing() || isLoading()) {
            return;
        } else {
            isRefreshing.set(true);
        }
        if (callback == null) {
            isRefreshing.set(false); // reset state
            return;
        }
        if (useNetwork && !hasNetwork()) {
//      EventBus.getDefault().post(new NetworkEvent("net error"));
            isRefreshing.set(false); // reset state
            return;
        }

        // ready to start
        resetPage();
        if (callback instanceof ListLoadCallback) {
            ((ListLoadCallback) callback).onRefreshStart();
        } else {
            isRefreshing.set(false); // reset state
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
        if (isRefreshing() || isLoading()) {
            return;
        } else {
            isLoading.set(true);
        }
        if (callback == null) {
            isLoading.set(false); // reset state
            return;
        }

        boolean lazyLoad = isLazyLoadEnabled() && lazyMode;
        boolean normalLoad = !isLazyLoadEnabled() && !lazyMode;

        if (!isDataLoaded() && !isDataEnd() && (lazyLoad || normalLoad)) {
            if (useNetwork && !hasNetwork()) {
//        EventBus.getDefault().post(new NetworkEvent("net error"));
                isLoading.set(false); // reset state
                return;
            }

            callback.onLoadStart();
        } else {
            isLoading.set(false);
        }
    }

    /**
     * you shouldn't manually call this only if you understanding what you are doing.
     */
    public void forceLoad() {
        if (isRefreshing() || isLoading()) {
            return;
        } else {
            isLoading.set(true);
        }
        if (callback == null) {
            isLoading.set(false); // reset state
            return;
        }
        if (useNetwork && !hasNetwork()) {
//      EventBus.getDefault().post(new NetworkEvent("net error"));
            isLoading.set(false); // reset state
            return;
        }

        if (!isDataEnd()) {
            callback.onLoadStart();
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
        isLazyLoadEnabled = false;
        isLoading.set(false);
        isRefreshing.set(false);
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

    public boolean isRefreshing() {
        return isRefreshing.get();
    }

    public void useNetwork(boolean useNetwork) {
        this.useNetwork = useNetwork;
    }

    // ########### Functions ###########

    /**
     * determine data is load finished or not
     *
     * @param list      data
     * @param threshold when data size is less than threshold, you can considered the data as end
     */
    public void handleResult(@Nullable List list, int threshold) {
        if (list == null || list.size() <= threshold) {
            setDataEnd(true);
            notifyPageLoadFailed();
        } else {
            setDataEnd(false);
            notifyPageLoaded();
        }
    }

    public void notifyPageLoaded() {
        if (callback != null && callback instanceof ListLoadCallback) {
            mPage++;
        }
        setDataLoaded(true);
        notifyLoadCallback();
    }

    public void notifyPageLoadFailed() {
        setDataLoaded(false);
        notifyLoadCallback();
    }

    private void notifyLoadCallback() {
        if (callback != null && callback instanceof ListLoadCallback) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                Observable.just(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override public void call(Integer integer) {
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
        savedInstanceState.putBoolean(REFRESHING_STATE, isRefreshing());
        savedInstanceState.putBoolean(DATA_LOAD_STATE, isDataLoaded);
        savedInstanceState.putBoolean(DATA_END_STATE, isDataEnd);
        savedInstanceState.putBoolean(LAZY_LOAD_STATE, isLazyLoadEnabled);
        savedInstanceState.putBoolean(NETWORK_STATE, useNetwork);
    }

    public void onRestoreState(Bundle savedInstanceState) {
        mPage = savedInstanceState.getInt(PAGE_STATE);
        isLoading.set(savedInstanceState.getBoolean(LOADING_STATE));
        isRefreshing.set(savedInstanceState.getBoolean(REFRESHING_STATE));
        savedInstanceState.putBoolean(DATA_LOAD_STATE, isDataLoaded);
        savedInstanceState.putBoolean(DATA_END_STATE, isDataEnd);
        savedInstanceState.putBoolean(LAZY_LOAD_STATE, isLazyLoadEnabled);
        savedInstanceState.putBoolean(NETWORK_STATE, useNetwork);
    }

    public interface LoadCallback {
        /**
         * load data in this callback, should be called on setUserVisibleHint() at first time and later
         * on onResume()
         */
        void onLoadStart();
    }

    public interface ListLoadCallback extends LoadCallback {
        /**
         * setRefresh data list due to loadType, should be called in onLoadStart().
         */
        void onRefreshStart();

        void notifyDataLoaded();
    }
}