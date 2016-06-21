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
import com.xxxifan.devbox.library.util.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by xifan on 4/6/16.
 */
public abstract class BaseFragment extends Fragment {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

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
        View view = inflater.inflate(getLayoutId(), container, false);
        onSetupFragment(view, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
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
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        }
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Fragments.KEY_RESTORE, isVisible());
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
    protected void onVisible() {

    }

    //##########  Protected helper methods ##########
    @ColorInt
    public int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    public Drawable getCompatDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    protected void registerEventBus(Object object) {
        EventBus eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(object)) {
            eventBus.register(object);
        }
    }

    protected void unregisterEventBus(Object object) {
        EventBus eventBus = EventBus.getDefault();
        if (eventBus.isRegistered(object)) {
            eventBus.unregister(object);
        }
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
