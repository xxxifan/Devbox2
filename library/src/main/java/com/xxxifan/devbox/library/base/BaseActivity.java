package com.xxxifan.devbox.library.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.event.BaseEvent;
import com.xxxifan.devbox.library.util.IOUtils;
import com.xxxifan.devbox.library.util.ViewUtils;
import com.xxxifan.devbox.library.util.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import rx.Observable;
import rx.subjects.BehaviorSubject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xifan on 3/30/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int BASE_CONTAINER_ID = R.id._internal_base_container;
    public static final int BASE_TOOLBAR_STUB_ID = R.id._internal_toolbar_stub;
    public static final int BASE_TOOLBAR_SHADOW_ID = R.id._internal_toolbar_shadow;
    public static final int BASE_DRAWER_ID = R.id._internal_drawer_layout;
    public static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    private BackKeyListener mBackKeyListener;
    private DataLoader mDataLoader;

    private boolean mConfigured;
    private int mRootLayoutId;
    private boolean mRegisterEventBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onConfigActivity();
        mConfigured = true;
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);

        setActivityView(getLayoutId());
        onSetupActivity(savedInstanceState);

        if (getDataLoader() != null && savedInstanceState != null) {
            getDataLoader().onRestoreState(savedInstanceState);
        }
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
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();

        if (mRegisterEventBus) {
            EventBus eventBus = EventBus.getDefault();
            if (eventBus.isRegistered(this)) {
                eventBus.unregister(this);
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
        if (mBackKeyListener != null) {
            mBackKeyListener = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (getDataLoader() != null) {
            getDataLoader().onSavedState(outState);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackKeyListener == null || getSupportFragmentManager().getBackStackEntryCount() > 0
                || !mBackKeyListener.onPressed()) {
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
    public Context getContext() {
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

    /**
     * register EventBus on resume/pause by default, must be called onConfigActivity
     */
    @BeforeConfigActivity
    protected void registerEventBus() {
        mRegisterEventBus = true;
    }

    /**
     * use a data loader to control data load state
     *
     * @param useNetwork if is network data loader, it will not request if no network there.
     */
    @BeforeConfigActivity
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
     * Annotated methods should run in {@link #onConfigActivity()}
     */
    @Target(ElementType.METHOD)
    public @interface BeforeConfigActivity {
    }

    public interface BackKeyListener {
        boolean onPressed();
    }

}
