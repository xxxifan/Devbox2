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

package com.xxxifan.devbox.core.ui;

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.xxxifan.devbox.core.Devbox;
import com.xxxifan.devbox.core.R;
import com.xxxifan.devbox.core.base.uicomponent.ToolbarComponent;
import com.xxxifan.devbox.core.base.uicomponent.TranslucentBarComponent;
import com.xxxifan.devbox.core.base.uicomponent.UIComponent;
import com.xxxifan.devbox.core.util.ViewUtils;

import static com.xxxifan.devbox.core.base.uicomponent.TranslucentBarComponent.FIT_TOOLBAR;
import static com.xxxifan.devbox.core.base.uicomponent.TranslucentBarComponent.FIT_WINDOW_BOTH;
import static com.xxxifan.devbox.core.base.uicomponent.TranslucentBarComponent.FIT_WINDOW_TOP;


/**
 * Translucent status bar version of ToolbarActivity
 * add additional status bar height to toolbar and content,
 * so it can't display any content in status bar
 * <p/>
 * Created by xifan on 4/5/16.
 */
public abstract class TranslucentActivity extends ToolbarActivity {

    private TranslucentBarComponent mTranslucentComponent;
    private ToolbarComponent mToolbarComponent;

    @Override @SuppressLint("NewApi")
    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        super.attachContentView(containerView, layoutResID);

        // override toolbar
        mToolbarComponent = new ToolbarComponent() {
            @Override protected void setupToolbar(View containerView, View toolbarView) {
                super.setupToolbar(containerView, toolbarView);
                if (mTranslucentComponent.getFitWindowMode() != FIT_WINDOW_BOTH) {
                    View contentView = ((ViewGroup) containerView).getChildAt(0);
                    if (mTranslucentComponent.getFitWindowMode() == FIT_TOOLBAR) {
                        int toolbarHeight = toolbarView.getResources()
                                .getDimensionPixelSize(R.dimen.toolbar_height);
                        int toolbarOffset = Devbox.isKitkat() ? ViewUtils.getSystemBarHeight() : 0;
                        int topMargin = ((MarginLayoutParams) contentView.getLayoutParams()).topMargin;
                        ((MarginLayoutParams) contentView.getLayoutParams()).topMargin =
                                Math.max(topMargin, toolbarHeight + toolbarOffset);
                    } else if (mTranslucentComponent.getFitWindowMode() == FIT_WINDOW_TOP) {
                        ((MarginLayoutParams) contentView.getLayoutParams()).topMargin = 0;
                    }
                }
            }
        };

        addUIComponents(mToolbarComponent, new TranslucentBarComponent(this));
    }

    @Override
    protected void inflateComponents(View containerView, ArrayMap<String, UIComponent> uiComponents) {
        // update components, they may be removed before inflate.
        ToolbarComponent toolbarComponent = getUIComponent(ToolbarComponent.TAG);
        TranslucentBarComponent translucentBarComponent = getUIComponent(TranslucentBarComponent.TAG);

        if (mToolbarComponent != null && toolbarComponent != null) {
            toolbarComponent.loadConfig(mToolbarComponent);
        }
        if (mTranslucentComponent != null && translucentBarComponent != null) {
            translucentBarComponent.loadConfig(mTranslucentComponent);
        }
        mToolbarComponent = toolbarComponent;
        mTranslucentComponent = translucentBarComponent;

        super.inflateComponents(containerView, uiComponents);
    }

    public void setFitSystemWindowMode(@TranslucentBarComponent.FitWindowMode int mode) {
        if (mTranslucentComponent != null) {
            mTranslucentComponent.setFitSystemWindowMode(mode);
        }
    }

    /**
     * make toolbar transparent, due to toolbar_container which has a shadow,
     * we can't simply make toolbar transparent by toolbar.setBackgroundColor()
     */
    public void transparentToolbar() {
        if (mToolbarComponent != null) {
            mToolbarComponent.transparentToolbar(this);
        }
    }

    /**
     * switch to full transparent status bar immediately, or configured in onConfigureActivity()
     */
    public void transparentStatusBar() {
        if (mTranslucentComponent != null) {
            mTranslucentComponent.transparentStatusBar();
        }
    }

    public void translucentNavBar() {
        if (mTranslucentComponent != null) {
            mTranslucentComponent.translucentNavBar();
        }
    }

    @BeforeConfigActivity public void lightStatusBar() {
        if (mTranslucentComponent != null) {
            mTranslucentComponent.lightStatusBar();
        }
    }


    protected void enableStatusBarHint(boolean enable) {
        if (mTranslucentComponent != null) {
            mTranslucentComponent.enableStatusBarHint(enable);
        }
    }

    @TranslucentBarComponent.FitWindowMode
    public int getFitWindowMode() {
        return mTranslucentComponent == null ? FIT_TOOLBAR : mTranslucentComponent.getFitWindowMode();
    }

    @SuppressLint("NewApi") @Override protected void onDestroy() {
        if (mTranslucentComponent != null) {
            mTranslucentComponent.onDestroy();
        }
        super.onDestroy();
    }
}
