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
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.event.BaseEvent;
import com.xxxifan.devbox.library.util.IOUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xifan on 3/30/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int BASE_CONTAINER_ID =  R.id._internal_base_container;
    public static final int BASE_TOOLBAR_STUB_ID = R.id._internal_toolbar_stub;
    public static final int BASE_TOOLBAR_SHADOW_ID = R.id._internal_toolbar_shadow;
    public static final int BASE_DRAWER_ID = R.id._internal_drawer_layout;
    public static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    private BackKeyListener mBackKeyListener;

    private List<UiController> mUiControllers;
    private boolean mConfigured;
    private int mRootLayoutId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onConfigActivity();
        mConfigured = true;
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);

        setActivityView(getLayoutId());
        onSetupActivity(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // if root layout has been set, then it's a container, so let subclass
        // to handle content view.
        if (mRootLayoutId == 0) {
            mRootLayoutId = layoutResID;
        }
        super.setContentView(mRootLayoutId);
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
        if (mBackKeyListener != null) {
            mBackKeyListener = null;
        }
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

    @Override
    public void onBackPressed() {
        if (mBackKeyListener == null || !mBackKeyListener.onPressed()) {
            super.onBackPressed();
        }
    }

    //##########  Protected construct methods  ##########
    protected void onConfigActivity() {
        //Stub
    }

    protected void setActivityView(@LayoutRes int layoutResID) {
        setContentView(layoutResID);
    }

    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        if (containerView == null) {
            throw new IllegalStateException("Cannot find container view");
        }
        if (layoutResID == 0) {
            throw new IllegalStateException("Invalid layout id");
        }
        View contentView = getLayoutInflater().inflate(layoutResID, null, false);
        if (containerView instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
            ((ViewGroup) containerView).addView(contentView, 0, params);
        } else {
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
            ((ViewGroup) containerView).addView(contentView, 0, params);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T $(int viewId) {
        return (T) findViewById(viewId);
    }

    @BeforeConfigActivity
    protected void setRootLayoutId(@LayoutRes int rootLayoutId) {
        checkConfigured();
        mRootLayoutId = rootLayoutId;
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

    protected boolean isConfigured() {
        return mConfigured;
    }

    protected void checkConfigured() {
        if (mConfigured) {
            throw new IllegalStateException("You must call this method in onConfigActivity");
        }
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

    public void setBackKeyListener(BackKeyListener listener) {
        mBackKeyListener = listener;
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
     * Annotated methods should run in {@link #onConfigActivity()}
     */
    @Target(ElementType.METHOD)
    public @interface BeforeConfigActivity {
    }

    public interface BackKeyListener {
        boolean onPressed();
    }

}
