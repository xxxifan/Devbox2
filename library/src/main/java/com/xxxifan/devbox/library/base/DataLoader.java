package com.xxxifan.devbox.library.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    private LoadCallback callback;

    /* indicate whether data is loaded */
    private boolean isDataLoaded;
    /* indicate whether data is end */
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

    private void onDataLoad(boolean isLazyLoadMode) {
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

        if (!isDataLoaded() && !isDataEnd()) {
            if (isLazyLoadEnabled() && isLazyLoadMode || !isLazyLoadEnabled() && !isLazyLoadMode) {
                Logger.d("onLoadStart");
                boolean isDataLoaded = callback.onLoadStart();
                setDataLoaded(isDataLoaded);
            }
        }
    }

    public boolean isDataLoaded() {
        return isDataLoaded;
    }

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
            mPage += 1;
        }
        isLoading.set(false);
        setDataLoaded(true);
    }

    public int getPage() {
        return mPage;
    }

    public void disableDataLoad() {
        setDataLoaded(true);
        setDataEnd(true);
    }

    public void resetPage() {
        mPage = 1;
    }

    public void destroy() {
        callback = null;
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
