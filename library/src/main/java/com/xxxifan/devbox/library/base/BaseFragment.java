package com.xxxifan.devbox.library.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.xxxifan.devbox.library.event.BaseEvent;
import com.xxxifan.devbox.library.util.IOUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by xifan on 4/6/16.
 */
public abstract class BaseFragment extends Fragment {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    private List<ChildUiController> mUiControllers;

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

        // handle controller event
        if (mUiControllers != null && mUiControllers.size() > 0) {
            for (int i = 0; i < mUiControllers.size(); i++) {
                mUiControllers.get(i).onResume();
            }
        }
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();

        // handle controller event
        if (mUiControllers != null && mUiControllers.size() > 0) {
            for (int i = 0; i < mUiControllers.size(); i++) {
                mUiControllers.get(i).onPause();
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
        unregisterUiControllers();
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

    private void unregisterUiControllers() {
        // unregister ui controllers
        if (mUiControllers != null && mUiControllers.size() > 0) {
            for (int i = 0; i < mUiControllers.size(); i++) {
                mUiControllers.get(i).onDestroy();
            }
            mUiControllers.clear();
            mUiControllers = null;
        }
    }

    //########## Protected construct methods ##########
    protected void onBundleReceived(Bundle data) {

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

    /**
     * register controllers, so that BaseFragment can do some lifecycle work automatically
     */
    protected void registerUiController(ChildUiController... controllers) {
        if (controllers == null) {
            Log.e(getSimpleName(), "controllers cannot be empty");
            return;
        }

        if (mUiControllers == null) {
            mUiControllers = new ArrayList<>();
        }
        for (int i = 0, s = controllers.length; i < s; i++) {
            mUiControllers.add(controllers[i]);
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

    //##########  Abstract methods  ###########
    protected abstract int getLayoutId();

    protected abstract void onSetupFragment(View view, Bundle savedInstanceState);

    public abstract String getSimpleName();
}
