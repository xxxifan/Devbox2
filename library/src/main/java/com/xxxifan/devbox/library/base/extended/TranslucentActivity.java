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

package com.xxxifan.devbox.library.base.extended;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.base.SystemBarTintManager;
import com.xxxifan.devbox.library.util.ViewUtils;


/**
 * Translucent status bar version of ToolbarActivity
 * add additional status bar height to toolbar and content,
 * so it can't display any content in status bar
 * <p/>
 * Created by xifan on 4/5/16.
 */
public abstract class TranslucentActivity extends ToolbarActivity {

    public static final int FIT_NONE = 0;
    public static final int FIT_TOOLBAR = 1;
    public static final int FIT_WINDOW = 2;

    private SystemBarTintManager mSystemBarManager;
    private boolean mTransparentStatusBar;
    private boolean mTranslucentNavBar;
    private int mFitWindowMode;

    @Override protected void onConfigureActivity() {
        super.onConfigureActivity();
        setFitSystemWindowMode(FIT_WINDOW);
    }

    @Override
    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        super.attachContentView(containerView, layoutResID);
        if (mFitWindowMode == FIT_WINDOW) {
            ((MarginLayoutParams) containerView.getLayoutParams()).topMargin = ViewUtils.getSystemBarHeight();
        } else if (mFitWindowMode == FIT_TOOLBAR) {
            View contentView = ((ViewGroup) containerView).getChildAt(0);
            MarginLayoutParams layoutParams = ((MarginLayoutParams) contentView.getLayoutParams());
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_height)
                    + ViewUtils.getSystemBarHeight();
        }
        setTranslucentBar();
    }

    @Override protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        if (mFitWindowMode == FIT_TOOLBAR) {
            // use margin to fit system window.
            ((MarginLayoutParams) toolbarView.getLayoutParams()).topMargin = ViewUtils.getSystemBarHeight();
        }
    }

    /**
     * @param mode one of {@link #FIT_NONE}, {@link #FIT_TOOLBAR}, {@link #FIT_WINDOW},
     *             default {@link #FIT_WINDOW}.
     */
    protected void setFitSystemWindowMode(int mode) {
        mFitWindowMode = mode;
    }

    /**
     * make toolbar transparent, due to toolbar_container which has a shadow,
     * we can't simply make toolbar transparent by toolbar.setBackgroundColor()
     */
    protected void transparentToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            $(BASE_TOOLBAR_SHADOW_ID).setVisibility(View.GONE);
        }
    }

    /**
     * switch to full transparent status bar immediately, or configured in onConfigureActivity()
     */
    protected void transparentStatusBar() {
        mTransparentStatusBar = true;
        if (isConfigured()) {
            setTranslucentBar();
        }
    }

    protected void translucentNavBar() {
        mTranslucentNavBar = true;
        if (isConfigured()) {
            setTranslucentBar();
        }
    }

    protected void lightStatusBar() {
        ViewUtils.setStatusBarLightMode(this, true);
    }

    protected boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    protected boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    protected void setTranslucentBar() {
        // setup translucent bar for kitkat devices
        if (!isKitkat()) {
            return;
        }

        if (isKitkat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (mTranslucentNavBar) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            if (mSystemBarManager == null) {
                mSystemBarManager = new SystemBarTintManager(this);
            }
            mSystemBarManager.setStatusBarTintEnabled(true);
            mSystemBarManager.setTintColor(getCompatColor(R.color.colorPrimary));
        }

        if (isLollipop()) {
            // always use transparent status bar
            Window window = getWindow();
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
}
