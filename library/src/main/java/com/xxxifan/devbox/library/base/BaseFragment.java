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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.xxxifan.devbox.library.event.BaseEvent;
import com.xxxifan.devbox.library.util.Fragments;
import com.xxxifan.devbox.library.util.IOUtils;
import com.xxxifan.devbox.library.util.Tests;
import com.xxxifan.devbox.library.util.ViewUtils;
import com.xxxifan.devbox.library.util.logger.Logger;

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

        onSetupFragment(view, savedInstanceState);

        if (getDataLoader() != null && savedInstanceState != null) {
            getDataLoader().onRestoreState(savedInstanceState);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
            if (mDataLoader != null) {
                Logger.d("mDataLoader startLazyLoad called on onVisible");
                mDataLoader.startLazyLoad();
            }
        }
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
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
        if (getDataLoader() != null) {
            getDataLoader().onSavedState(outState);
        }
    }

    private void restoreFragmentState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
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
    protected void onVisible() {}

    //##########  Protected helper methods ##########
    @ColorInt
    public int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    public Drawable getCompatDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
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

    protected DataLoader getDataLoader() {
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
        return RxLifecycle.bindFragment(lifecycleSubject);
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
}
