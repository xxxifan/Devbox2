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

package com.xxxifan.devbox.core.base.uicomponent;

import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.xxxifan.devbox.core.R;
import com.xxxifan.devbox.core.base.BaseActivity;
import com.xxxifan.devbox.core.util.ViewUtils;


/**
 * Created by xifan on 12/21/16.
 */

public class ToolbarComponent implements UIComponent {
    public static final String TAG = "ToolbarComponent";

    private boolean mUseLightToolbar;

    public ToolbarComponent() {
    }

    public ToolbarComponent(boolean useLightToolbar) {
        mUseLightToolbar = useLightToolbar;
    }

    @Override public void inflate(View containerView) {
        ViewStub toolbarStub = (ViewStub) containerView.findViewById(BaseActivity.BASE_TOOLBAR_STUB_ID);
        if (toolbarStub != null) {
            toolbarStub.setLayoutResource(mUseLightToolbar ? R.layout._internal_view_toolbar_light : R.layout._internal_view_toolbar_dark);
            toolbarStub.inflate();
            View toolbarView = containerView.findViewById(BaseActivity.BASE_TOOLBAR_ID);
            if (toolbarView != null) {
                setupToolbar(containerView, toolbarView);
            } else {
                throw new IllegalStateException("Can't find toolbar");
            }
        }
    }

    protected void setupToolbar(View containerView, View toolbarView) {
        BaseActivity activity = ((BaseActivity) containerView.getContext());
        if (activity.getSupportActionBar() instanceof WindowDecorActionBar) {
            throw new IllegalStateException("You must make your app theme extends from Devbox.AppTheme or manually set windowActionBar to false.");
        }

        // fix content position if toolbar exists.
        View contentView = ((ViewGroup) containerView).getChildAt(0);
        ((ViewGroup.MarginLayoutParams) contentView.getLayoutParams()).topMargin = toolbarView
                .getResources().getDimensionPixelSize(R.dimen.toolbar_height);

        // set support actionbar
        Toolbar toolbar = (Toolbar) toolbarView;
        toolbar.setBackgroundColor(ViewUtils.getCompatColor(R.color.colorPrimary));
        activity.setSupportActionBar(toolbar);

        // set home as up key
        if (!activity.isTaskRoot() && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void useLightToolbar() {
        mUseLightToolbar = true;
    }

    @Override public String getTag() {
        return TAG;
    }
}
