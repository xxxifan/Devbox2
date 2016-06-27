package com.xxxifan.devbox.library.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.xxxifan.devbox.library.Devbox;
import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.event.NetworkEvent;
import com.xxxifan.devbox.library.util.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicBoolean;

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

    public DataLoader(LoadCallback callbacks) {
        isLoading = new AtomicBoolean(false);
        setCallback(callbacks);
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
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isAvailable();
    }

    private void setCallback(LoadCallback callback) {
        if (this.callback != null && callback != null) {
            Logger.e("You have set a callback already, did you really want to set it again?");
        }
        this.callback = callback;
    }

    public void startRefresh() {
        if (isLoading.get()) {
            Logger.d("load is in progress, dismiss");
            return;
        } else {
            isLoading.set(true);
        }
        if (callback == null) {
            Logger.d("load callback is null");
            isLoading.set(false); // reset state
            return;
        }
        if (useNetwork && !hasNetwork()) {
            NetworkEvent event = new NetworkEvent(
                    Devbox.getAppDelegate().getString(R.string.msg_network_unavailable));
            EventBus.getDefault().post(event);
            isLoading.set(false); // reset state
            return;
        }

        // ready to start
        if (callback instanceof ListLoadCallback) {
            Logger.d("onRefreshStart");
            ((ListLoadCallback) callback).onRefreshStart();
        } else {
            Logger.d("callback should be ListLoadCallback, call startLoad() instead now");
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
        // reset lazy load, it will only call once
        isLazyLoadEnabled = false;

        if (!isLoading.get()) {
            isLoading.set(true);
        } else {
            Logger.d("load is in progress, dismiss");
            return;
        }
        if (callback == null) {
            Logger.d("load callback is null");
            isLoading.set(false); // reset state
            return;
        }
        if (useNetwork && !hasNetwork()) {
            NetworkEvent event = new NetworkEvent(
                    Devbox.getAppDelegate().getString(R.string.msg_network_unavailable));
            EventBus.getDefault().post(event);
            isLoading.set(false); // reset state
            return;
        }

        boolean lazyLoad = isLazyLoadEnabled() && lazyMode;
        boolean normalLoad = !isLazyLoadEnabled() && !lazyMode;

        if (!isDataLoaded() && !isDataEnd() && (lazyLoad || normalLoad)) {
            Logger.d("onLoadStart");
            boolean isDataLoaded = callback.onLoadStart();
            setDataLoaded(isDataLoaded);
        } else {
            isLoading.set(false);
            Logger.d("load dismiss");
        }
    }

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
        isLazyLoadEnabled = true;
    }

    public boolean isLoading() {
        return isLoading.get();
    }

    public void useNetwork(boolean useNetwork) {
        this.useNetwork = useNetwork;
    }

    public void notifyPageLoaded() {
        if (callback != null && callback instanceof ListLoadCallback) {
            mPage++;
        }
        isLoading.set(false);
        setDataLoaded(true);
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
    }
}
