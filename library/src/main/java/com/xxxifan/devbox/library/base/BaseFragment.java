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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.event.BaseEvent;
import com.xxxifan.devbox.library.util.Fragments;
import com.xxxifan.devbox.library.util.IOUtils;
import com.xxxifan.devbox.library.util.StatisticsUtil;
import com.xxxifan.devbox.library.util.Tests;
import com.xxxifan.devbox.library.util.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by xifan on 4/6/16.
 */
public abstract class BaseFragment extends Fragment {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    private DataLoader mDataLoader;
    private boolean mRegisterEventBus;

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);

        setHasOptionsMenu(true);
        Bundle data = getArguments();
        if (data != null) {
            onBundleReceived(data);
        }
        restoreFragmentState(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        Cannon.load(view);

        onSetupFragment(view, savedInstanceState);

        if (getDataLoader() != null) {
            if (savedInstanceState != null) {
                getDataLoader().onRestoreState(savedInstanceState);
            }

            view.post(new Runnable() {
                @Override public void run() {
                    if (getUserVisibleHint() && getDataLoader().isLazyLoadEnabled()) {
                        setUserVisibleHint(true);
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
        StatisticsUtil.onPageStart(getContext(), getSimpleName());

        // register eventBus
        if (mRegisterEventBus) {
            EventBus eventBus = EventBus.getDefault();
            if (!eventBus.isRegistered(this)) {
                eventBus.register(this);
            }
        }

        // handle data loader
        if (mDataLoader != null) {
            mDataLoader.startLoad();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
            if (mDataLoader != null) {
                mDataLoader.startLazyLoad();
            }
        }
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        StatisticsUtil.onPageEnd();
        super.onPause();

        if (mRegisterEventBus) {
            EventBus eventBus = EventBus.getDefault();
            if (eventBus.isRegistered(this)) {
                eventBus.unregister(this);
            }
        }
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
        Cannon.reset();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
        if (mDataLoader != null) {
            mDataLoader.destroy();
            mDataLoader = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Fragments.KEY_RESTORE, isVisible());
        outState.putBoolean(Fragments.KEY_RESTORE_VIEWPAGER,
                            getView() != null && getView().getParent() instanceof ViewPager);
        if (getDataLoader() != null) {
            getDataLoader().onSavedState(outState);
        }
    }

    private void restoreFragmentState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Fragments.KEY_RESTORE_VIEWPAGER, false)) {
                return;
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (savedInstanceState.getBoolean(Fragments.KEY_RESTORE, false)) {
                transaction.show(this);
            } else {
                transaction.hide(this);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    //########## Protected construct methods ##########

    /**
     * called when onCreate and fragment has Arguments
     */
    protected void onBundleReceived(Bundle data) {

    }

    /**
     * manual control method for sometimes lifecycle not working for fragment.
     */
    public void onVisible() {}

    //##########  Protected helper methods ##########
    @ColorInt
    public int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    public Drawable getCompatDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    /**
     * attach a toolbar in fragment
     *
     * @param rootView  root view that toolbar attach to
     * @param darkTheme dark theme used in colorful toolbar with white title, light theme in light background with dark title.
     * @return true if success
     */
    protected boolean attachToolbar(View rootView, String title, @ColorInt int toolbarColor, boolean darkTheme) {
        if (isAdded() && getView() != null) {
            View toolbarView = View.inflate(getContext(), darkTheme ? R.layout._internal_view_toolbar_dark : R.layout._internal_view_toolbar_light, null);
            toolbarView.setBackgroundColor(toolbarColor);
            ((Toolbar) toolbarView).setTitle(title);
            ((ViewGroup) rootView).addView(toolbarView, 0);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T $(View view, int viewId) {
        return (T) view.findViewById(viewId);
    }

    /**
     * register EventBus on resume/pause by default, must be called before onResume/onPause
     */
    protected void registerEventBus() {
        Tests.checkBoolean(!isResumed());
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

    public DataLoader getDataLoader() {
        return mDataLoader;
    }

    protected void postEvent(BaseEvent event, Class target) {
        EventBus.getDefault().post(event);
    }

    protected void postStickyEvent(BaseEvent event, Class target) {
        EventBus.getDefault().postSticky(event);
    }

    protected final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    protected final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    protected final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bind(lifecycleSubject);
    }

    protected <T> Observable.Transformer<T, T> io() {
        return IOUtils.io();
    }

    protected <T> Observable.Transformer<T, T> computation() {
        return IOUtils.computation();
    }

    /**
     * Override {@link BaseView#showMessage(String)}
     */
    public void showMessage(String msg) {
        ViewUtils.showToast(msg);
    }

    //##########  Abstract methods  ###########
    protected abstract int getLayoutId();

    protected abstract void onSetupFragment(View view, Bundle savedInstanceState);

    public abstract String getSimpleName();

    /**
     * Stupid thing to post task
     */
    protected static class Cannon {
        static View mView;

        static void load(View view) {
            mView = view;
        }

        static void reset() {
            mView = null;
        }

        public static void post(Runnable runnable) {
            if (mView != null) {
                mView.post(runnable);
            }
        }

        public static void postDelayed(Runnable runnable, int delay) {
            if (mView != null) {
                mView.postDelayed(runnable, delay);
            }
        }
    }
}
