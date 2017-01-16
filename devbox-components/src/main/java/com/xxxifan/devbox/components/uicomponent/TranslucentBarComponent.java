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

package com.xxxifan.devbox.components.uicomponent;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.xxxifan.devbox.components.R;
import com.xxxifan.devbox.core.base.BaseActivity;
import com.xxxifan.devbox.core.base.SystemBarTintManager;
import com.xxxifan.devbox.core.base.uicomponent.UIComponent;
import com.xxxifan.devbox.core.util.ViewUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;

import static com.xxxifan.devbox.core.Devbox.isKitkat;
import static com.xxxifan.devbox.core.Devbox.isLollipop;

/**
 * Created by xifan on 1/6/17.
 */

public class TranslucentBarComponent implements UIComponent {
    public static final String TAG = "TranslucentBarComponent";

    public static final int FIT_NONE = -1;
    public static final int FIT_TOOLBAR = 0;
    public static final int FIT_WINDOW_TOP = 1;
    public static final int FIT_WINDOW_BOTH = 2;

    private SystemBarTintManager mSystemBarManager;
    private boolean mTransparentStatusBar;
    private boolean mTranslucentNavBar;
    private int mFitWindowMode;
    private boolean mDisableStatusBarHint;

    private WeakReference<BaseActivity> mActivityRef;


    public TranslucentBarComponent(BaseActivity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    @Override @SuppressLint("NewApi") public void inflate(View containerView) {
        if (getFitWindowMode() != FIT_WINDOW_BOTH) {
            if (isLollipop()) {
                mActivityRef.get().getWindow().getDecorView()
                        .setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                            @Override
                            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                                setWindowOffset(
                                        mActivityRef.get().findViewById(BaseActivity.BASE_CONTAINER_ID),
                                        insets.getStableInsetTop()
                                );
                                return v.onApplyWindowInsets(insets);
                            }
                        });
            } else if (isKitkat()) {
                setWindowOffset(
                        containerView,
                        ViewUtils.getSystemBarHeight()
                );
            }
        }

        setTranslucentBar();
    }

    @SuppressLint("InlinedApi") protected void setTranslucentBar() {
        // setup translucent bar for kitkat devices
        if (!isKitkat()) {
            return;
        }

        BaseActivity activity = mActivityRef.get();
        if (activity == null || !isKitkat()) {
            return;
        }

        // translucent status bar
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // translucent nav bar
        if (mTranslucentNavBar && getFitWindowMode() != FIT_WINDOW_BOTH) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        // status bar tint
        if (mSystemBarManager == null) {
            mSystemBarManager = new SystemBarTintManager(activity);
            mSystemBarManager.setTintColor(ViewUtils.getCompatColor(R.color.colorPrimary));
        }
        mSystemBarManager.setStatusBarTintEnabled(!mDisableStatusBarHint);

        // lollipop enhance
        if (isLollipop()) {
            // always use translucent status bar
            Window window = activity.getWindow();
            int uiFlag = window.getDecorView().getSystemUiVisibility() |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(uiFlag);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (mTransparentStatusBar) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     * set offset between container and window,
     * {@link #FIT_TOOLBAR} make both container and toolbar have space,
     * {@link #FIT_WINDOW_TOP} make container top have space with translucent bar,
     * {@link #FIT_WINDOW_BOTH} works like {@link View#setFitsSystemWindows(boolean)}
     *
     * @param containerView container view that need to fit window.
     * @param offset        pixels to margin
     */
    protected void setWindowOffset(View containerView, int offset) {
        if (getFitWindowMode() == FIT_WINDOW_TOP) {
//            ((MarginLayoutParams) containerView.getLayoutParams()).topMargin = 0;
        } else if (getFitWindowMode() == FIT_TOOLBAR) {
            View toolbar = mActivityRef.get().findViewById(BaseActivity.BASE_TOOLBAR_ID);
            if (toolbar != null) {
                ((ViewGroup.MarginLayoutParams) toolbar.getLayoutParams()).topMargin = offset;
            }
        } else if (getFitWindowMode() == FIT_WINDOW_BOTH) {
            containerView.setFitsSystemWindows(true);
        }
    }

    /**
     * only set for kitkat and newer apis, more see from {@link #setWindowOffset(View, int)}
     *
     * @param mode one of {@link #FIT_NONE}, {@link #FIT_TOOLBAR}, {@link #FIT_WINDOW_TOP},
     *             default {@link #FIT_WINDOW_BOTH}.
     */
    public void setFitSystemWindowMode(@FitWindowMode int mode) {
        mFitWindowMode = mode;
    }

    /**
     * switch to full transparent status bar immediately, or configured in onConfigureActivity()
     */
    public void transparentStatusBar() {
        mTransparentStatusBar = true;
        setTranslucentBar();
    }

    public void translucentNavBar() {
        mTranslucentNavBar = true;
        setTranslucentBar();
    }

    public void lightStatusBar() {
        if (mActivityRef.get() != null) {
            ViewUtils.setStatusBarLightMode(mActivityRef.get(), true);
        }
    }


    public void enableStatusBarHint(boolean enable) {
        mDisableStatusBarHint = !enable;
    }

    @FitWindowMode
    public int getFitWindowMode() {
        return mFitWindowMode;
    }

    @SuppressLint("NewApi") public void onDestroy() {
        if (mActivityRef.get() != null) {
            Window window = mActivityRef.get().getWindow();
            if (isLollipop() && window.getDecorView() != null) {
                window.getDecorView().setOnApplyWindowInsetsListener(null);
            }
        }
    }

    @Override public String getTag() {
        return TAG;
    }

    @Override public <T extends UIComponent> void loadConfig(T component) {
        if (component instanceof TranslucentBarComponent) {
            TranslucentBarComponent oldComponent = (TranslucentBarComponent) component;
            mDisableStatusBarHint = oldComponent.mDisableStatusBarHint;
            mFitWindowMode = oldComponent.mFitWindowMode;
            mTranslucentNavBar = oldComponent.mTranslucentNavBar;
            mTransparentStatusBar = oldComponent.mTransparentStatusBar;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    @IntDef({FIT_NONE, FIT_TOOLBAR, FIT_WINDOW_TOP, FIT_WINDOW_BOTH})
    public @interface FitWindowMode {}
}
