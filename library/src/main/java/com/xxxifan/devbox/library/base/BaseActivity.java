package com.xxxifan.devbox.library.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.event.BaseEvent;
import com.xxxifan.devbox.library.util.IOUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xifan on 3/30/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    private List<UiController> mUiControllers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onConfigActivity();
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        setActivityView(getLayoutId());
        onSetupActivity(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);

        // handle controller event
        if (mUiControllers != null && mUiControllers.size() > 0) {
            for (int i = 0; i < mUiControllers.size(); i++) {
                mUiControllers.get(i).onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();

        // handle controller event
        if (mUiControllers != null && mUiControllers.size() > 0) {
            for (int i = 0; i < mUiControllers.size(); i++) {
                mUiControllers.get(i).onPause();
            }
        }
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
        unregisterUiControllers();
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

    //##########  Protected construct methods  ##########
    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, null, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
        // TODO: 4/5/16 Transparent support
        ((FrameLayout) containerView).addView(view, 0, params);
    }

    protected <T extends View> T $(int viewId) {
        return (T) findViewById(viewId);
    }

    protected void onConfigActivity() {
        //Stub
    }

    protected void setActivityView(@LayoutRes int layoutResID) {
        setContentView(layoutResID);
    }

    //##########  Protected helper methods ##########
    protected Context getContext() {
        return this;
    }

    @ColorInt
    public int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }

    public Drawable getCompatDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(this, resId);
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
     * register controllers, so that BaseActivity can do some lifecycle work automatically
     */
    protected void registerUiControllers(UiController... controllers) {
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

    public Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    public <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bindActivity(lifecycleSubject);
    }

    protected <T> Observable.Transformer<T, T> io() {
        return IOUtils.io();
    }

    protected <T> Observable.Transformer<T, T> computation() {
        return IOUtils.computation();
    }

    //##########  Abstract methods  ###########
    protected abstract int getLayoutId();

    protected abstract void onSetupActivity(Bundle savedInstanceState);

    public abstract String getSimpleName();

}
