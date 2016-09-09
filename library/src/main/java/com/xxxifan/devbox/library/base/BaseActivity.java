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

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.event.BaseEvent;
import com.xxxifan.devbox.library.util.IOUtils;
import com.xxxifan.devbox.library.util.StatisticsUtil;
import com.xxxifan.devbox.library.util.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import rx.Observable;
import rx.subjects.BehaviorSubject;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * BaseActivity uses a special way to inflate views. Execute order is
 * {@link #onConfigureActivity()}  -> {@link #getLayoutId()} -> {@link #setActivityView(int)} ->
 * {@link #onSetupActivity(Bundle)}
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int BASE_CONTAINER_ID = R.id._internal_base_container;
    public static final int BASE_TOOLBAR_STUB_ID = R.id._internal_toolbar_stub;
    public static final int BASE_TOOLBAR_ID = R.id._internal_toolbar;
    public static final int BASE_TOOLBAR_SHADOW_ID = R.id._internal_toolbar_shadow;
    public static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    private BackKeyListener mBackKeyListener;
    private DataLoader mDataLoader;

    private boolean mConfigured;
    private int mRootLayoutId;
    private boolean mRegisterEventBus;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        onConfigureActivity();
        mConfigured = true;
        Cannon.load(this);
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);

        setActivityView(getLayoutId());
        onSetupActivity(savedInstanceState);

        if (getDataLoader() != null && savedInstanceState != null) {
            getDataLoader().onRestoreState(savedInstanceState);
        }
    }

    @Override public void setContentView(@LayoutRes int layoutResID) {
        // if root layout has been set, then it's a container, so let subclass
        // to handle content view.
        if (mRootLayoutId == 0) {
            mRootLayoutId = layoutResID;
        }
        super.setContentView(mRootLayoutId);
    }

    @Override protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
        StatisticsUtil.onPageStart(this, getSimpleName());

        // register eventBus
        if (mRegisterEventBus) {
            EventBus eventBus = EventBus.getDefault();
            if (!eventBus.isRegistered(this)) {
                eventBus.register(this);
            }
        }

        // handle data loader
        if (mDataLoader != null) {
            Logger.d("mDataLoader startLoad called on resume");
            mDataLoader.startLoad();
        }
    }

    @Override protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        StatisticsUtil.onPageEnd();
        super.onPause();

        if (mRegisterEventBus) {
            EventBus eventBus = EventBus.getDefault();
            if (eventBus.isRegistered(this)) {
                eventBus.unregister(this);
            }
        }
    }

    @Override protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        Cannon.reset();
        super.onDestroy();
        if (mBackKeyListener != null) {
            mBackKeyListener = null;
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getDataLoader() != null) {
            getDataLoader().onSavedState(outState);
        }
    }

    @Override @CallSuper public void onBackPressed() {
        if (mBackKeyListener == null || getSupportFragmentManager().getBackStackEntryCount() > 0
                || !mBackKeyListener.onPressed()) {
            super.onBackPressed();
        }
    }

    //##########  Protected construct methods  ##########
    protected void onConfigureActivity() {
        //Stub
    }

    protected void setActivityView(@LayoutRes int layoutResID) {
        setContentView(layoutResID);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T $(int viewId) {
        return (T) findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T $(View view, int viewId) {
        return (T) view.findViewById(viewId);
    }

    @BeforeConfigActivity protected void setRootLayoutId(@LayoutRes int rootLayoutId) {
        checkConfigured();
        mRootLayoutId = rootLayoutId;
    }

    //##########  Protected helper methods ##########
    public Context getContext() {
        return this;
    }

    @ColorInt public int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }

    public Drawable getCompatDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(this, resId);
    }

    protected boolean isConfigured() {
        return mConfigured;
    }

    protected void checkConfigured() {
        if (mConfigured) {
            throw new IllegalStateException("You must call this method in onConfigureActivity");
        }
    }

    /**
     * register EventBus on resume/pause by default
     */
    protected void registerEventBus() {
        mRegisterEventBus = true;
    }

    /**
     * use a data loader to control data load state
     *
     * @param useNetwork if is network data loader, it will not request if no network there.
     */
    protected DataLoader registerDataLoader(boolean useNetwork, DataLoader.LoadCallback callback) {
        mDataLoader = DataLoader.init(useNetwork, callback);
        return mDataLoader;
    }

    protected DataLoader getDataLoader() {
        return mDataLoader;
    }

    protected void postEvent(@NonNull BaseEvent event, Class target) {
        EventBus.getDefault().post(event);
    }

    protected void postStickyEvent(@NonNull BaseEvent event, Class target) {
        EventBus.getDefault().postSticky(event);
    }

    public Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    public <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    protected <T> Observable.Transformer<T, T> io() {
        return IOUtils.io();
    }

    protected <T> Observable.Transformer<T, T> computation() {
        return IOUtils.computation();
    }

    public void setBackKeyListener(BackKeyListener listener) {
        mBackKeyListener = listener;
    }

    /**
     * Override {@link BaseView#showMessage(String)}
     */
    public void showMessage(String msg) {
        ViewUtils.showToast(msg);
    }

    //##########  Abstract methods  ###########

    /**
     * @return ContentView ID
     */
    protected abstract int getLayoutId();

    /**
     * Do things like init views here.
     */
    protected abstract void onSetupActivity(Bundle savedInstanceState);

    /**
     * @return a solid name for current class, usually is TAG name.
     */
    public abstract String getSimpleName();

    /**
     * Annotated methods should run in {@link #onConfigureActivity()}
     */
    @Target(ElementType.METHOD) @Retention(SOURCE)
    public @interface BeforeConfigActivity {
    }

    public interface BackKeyListener {
        boolean onPressed();
    }

    /**
     * Stupid thing to post task
     */
    protected static class Cannon {
        static Activity mActivity;

        static void load(Activity activity) {
            mActivity = activity;
        }

        static void reset() {
            mActivity = null;
        }

        public static void post(Runnable runnable) {
            if (mActivity != null) {
                mActivity.getWindow().getDecorView().post(runnable);
            }
        }

        public static void postDelayed(Runnable runnable, int delay) {
            if (mActivity != null) {
                mActivity.getWindow().getDecorView().postDelayed(runnable, delay);
            }
        }
    }

}
