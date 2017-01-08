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

package com.xxxifan.devbox.components;

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.xxxifan.devbox.components.uicomponent.TranslucentBarComponent;
import com.xxxifan.devbox.core.Devbox;
import com.xxxifan.devbox.core.base.ToolbarActivity;
import com.xxxifan.devbox.core.base.uicomponent.ToolbarComponent;
import com.xxxifan.devbox.core.util.ViewUtils;

import static com.xxxifan.devbox.components.uicomponent.TranslucentBarComponent.FIT_TOOLBAR;
import static com.xxxifan.devbox.components.uicomponent.TranslucentBarComponent.FIT_WINDOW_BOTH;
import static com.xxxifan.devbox.components.uicomponent.TranslucentBarComponent.FIT_WINDOW_TOP;


/**
 * Translucent status bar version of ToolbarActivity
 * add additional status bar height to toolbar and content,
 * so it can't display any content in status bar
 * <p/>
 * Created by xifan on 4/5/16.
 */
public abstract class TranslucentActivity extends ToolbarActivity {

    private TranslucentBarComponent mBarComponent;

    @Override @SuppressLint("NewApi")
    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        super.attachContentView(containerView, layoutResID);

        // override toolbar
        ToolbarComponent toolbarComponent = new ToolbarComponent() {
            @Override protected void setupToolbar(View containerView, View toolbarView) {
                super.setupToolbar(containerView, toolbarView);
                if (mBarComponent.getFitWindowMode() != FIT_WINDOW_BOTH) {
                    View contentView = ((ViewGroup) containerView).getChildAt(0);
                    if (mBarComponent.getFitWindowMode() == FIT_TOOLBAR) {
                        int toolbarHeight = toolbarView.getResources()
                                .getDimensionPixelSize(R.dimen.toolbar_height);
                        int toolbarOffset = Devbox.isKitkat() ? ViewUtils.getSystemBarHeight() : 0;
                        int topMargin = ((MarginLayoutParams) contentView.getLayoutParams()).topMargin;
                        ((MarginLayoutParams) contentView.getLayoutParams()).topMargin =
                                Math.max(topMargin, toolbarHeight + toolbarOffset);
                    } else if (mBarComponent.getFitWindowMode() == FIT_WINDOW_TOP) {
                        ((MarginLayoutParams) contentView.getLayoutParams()).topMargin = 0;
                    }
                }
            }
        };
        addUIComponents(toolbarComponent, mBarComponent = new TranslucentBarComponent(this));
    }

    public void setFitSystemWindowMode(@TranslucentBarComponent.FitWindowMode int mode) {
        if (mBarComponent != null) {
            mBarComponent.setFitSystemWindowMode(mode);
        }
    }

    /**
     * make toolbar transparent, due to toolbar_container which has a shadow,
     * we can't simply make toolbar transparent by toolbar.setBackgroundColor()
     */
    public void transparentToolbar() {
        if (mBarComponent != null) {
            mBarComponent.transparentToolbar();
        }

    }

    /**
     * switch to full transparent status bar immediately, or configured in onConfigureActivity()
     */
    public void transparentStatusBar() {
        if (mBarComponent != null) {
            mBarComponent.transparentStatusBar();
        }
    }

    public void translucentNavBar() {
        if (mBarComponent != null) {
            mBarComponent.translucentNavBar();
        }
    }

    @BeforeConfigActivity public void lightStatusBar() {
        if (mBarComponent != null) {
            mBarComponent.lightStatusBar();
        }
    }


    protected void enableStatusBarHint(boolean enable) {
        if (mBarComponent != null) {
            mBarComponent.enableStatusBarHint(enable);
        }
    }

    @TranslucentBarComponent.FitWindowMode
    public int getFitWindowMode() {
        return mBarComponent == null ? FIT_TOOLBAR : mBarComponent.getFitWindowMode();
    }

    @SuppressLint("NewApi") @Override protected void onDestroy() {
        if (mBarComponent != null) {
            mBarComponent.onDestroy();
        }
        super.onDestroy();
    }
}
