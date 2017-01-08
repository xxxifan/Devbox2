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

package com.xxxifan.devbox.core.base;

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
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.xxxifan.devbox.core.R;
import com.xxxifan.devbox.core.base.uicomponent.UIComponent;
import com.xxxifan.devbox.core.event.BaseEvent;
import com.xxxifan.devbox.core.util.IOUtils;
import com.xxxifan.devbox.core.util.StatisticalUtil;
import com.xxxifan.devbox.core.util.ViewUtils;

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
    private ArrayMap<String, UIComponent> mUIComponents;

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
        if (mRootLayoutId > 0) {
            inflateComponents(getContainerView(), getUIComponents());
        }

        if (getDataLoader() != null && savedInstanceState != null) {
            getDataLoader().onRestoreState(savedInstanceState);
        }
    }

    @Override protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
        StatisticalUtil.onPageStart(this, getSimpleName());

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
        StatisticalUtil.onPageEnd();
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
        if (mDataLoader != null) {
            mDataLoader.destroy();
        }
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

    //##########  construct methods  ##########
    protected void onConfigureActivity() {
        //Stub
    }

    @BeforeConfigActivity protected void setRootLayoutId(@LayoutRes int rootLayoutId) {
        checkConfigured();
        mRootLayoutId = rootLayoutId;
    }

    protected void setActivityView(@LayoutRes int layoutResID) {
        // if root layout has been set, then it's a container, so let subclass
        // to handle content view.
        boolean hasNewRoot = mRootLayoutId > 0;
        setContentView(hasNewRoot ? mRootLayoutId : layoutResID);
        if (hasNewRoot) {
            attachContentView(getContainerView(), layoutResID);
        }
    }

    /**
     * Attach views to layout. It's good time to add UIComponent here.
     * @param containerView
     * @param layoutResID
     */
    @CallSuper protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        if (containerView == null) {
            throw new IllegalStateException("Cannot find container view");
        }
        if (layoutResID == 0) {
            throw new IllegalStateException("Invalid layout id");
        }
        View contentView = getLayoutInflater().inflate(layoutResID, null, false);
        ((ViewGroup) containerView).addView(contentView, 0);
    }

    private void inflateComponents(View containerView, ArrayMap<String, UIComponent> uiComponents) {
        if (uiComponents == null) {
            return;
        }
        for (int i = 0; i < uiComponents.size(); i++) {
            uiComponents.get(uiComponents.keyAt(i)).inflate(containerView);
        }
    }

    /**
     * Override {@link BaseView#showMessage(String)}
     */
    public void showMessage(String msg) {
        ViewUtils.showToast(msg);
    }

    //##########  Protected helper methods ##########
    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(int viewId) {
        return (T) findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(View view, int viewId) {
        return (T) view.findViewById(viewId);
    }

    public final Context getContext() {
        return this;
    }

    @ColorInt public final int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }

    public final Drawable getCompatDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(this, resId);
    }

    public final View getContainerView() {
        return ((ViewGroup) $(android.R.id.content)).getChildAt(0);
    }

    protected final boolean isConfigured() {
        return mConfigured;
    }

    protected final void checkConfigured() {
        if (mConfigured) {
            throw new IllegalStateException("You must call this method in onConfigureActivity");
        }
    }

    /**
     * register EventBus on resume/pause by default
     */
    protected final void registerEventBus() {
        mRegisterEventBus = true;
    }

    /**
     * use a data loader to control data load state
     *
     * @param useNetwork if is network data loader, it will not request if no network there.
     */
    protected final DataLoader registerDataLoader(boolean useNetwork, DataLoader.LoadCallback callback) {
        mDataLoader = DataLoader.init(useNetwork, callback);
        return mDataLoader;
    }

    /**
     * add UIComponents, it will use {@link UIComponent#getTag()} as name, same component will be replaced
     * should be called {@link #attachContentView(View, int)}
     *
     * @return current uiComponents
     */
    protected final ArrayMap<String, UIComponent> addUIComponents(UIComponent... uiComponents) {
        if (mUIComponents == null) {
            mUIComponents = new ArrayMap<>();
        }
        for (int i = 0, s = uiComponents.length; i < s; i++) {
            mUIComponents.put(uiComponents[i].getTag(), uiComponents[i]);
        }
        return mUIComponents;
    }

    protected final ArrayMap<String, UIComponent> getUIComponents() {
        return mUIComponents;
    }

    @SuppressWarnings("unchecked")
    protected final <T> T getUIComponent(String tag, Class<T> clazz) {
        if (mUIComponents != null) {
            return clazz.cast(mUIComponents.get(tag)) ;
        }
        return null;
    }

    protected final DataLoader getDataLoader() {
        return mDataLoader;
    }

    protected final void postEvent(@NonNull BaseEvent event, Class target) {
        EventBus.getDefault().post(event);
    }

    protected final void postStickyEvent(@NonNull BaseEvent event, Class target) {
        EventBus.getDefault().postSticky(event);
    }

    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    public final <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    protected final <T> Observable.Transformer<T, T> io() {
        return IOUtils.io();
    }

    protected final <T> Observable.Transformer<T, T> computation() {
        return IOUtils.computation();
    }

    public final void setBackKeyListener(BackKeyListener listener) {
        mBackKeyListener = listener;
    }

    //##########  Abstract methods  ###########

    /**
     * @return ContentView ID
     */
    protected abstract int getLayoutId();

    /**
     * Do everything start here.
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
