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

import android.support.annotation.LayoutRes;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.base.BaseActivity;


/**
 * Created by xifan on 4/5/16.
 */
public abstract class ToolbarActivity extends BaseActivity {

    private boolean mUseLightToolbar;

    @Override
    protected void onConfigActivity() {
        super.onConfigActivity();
        setRootLayoutId(R.layout._internal_activity_base);
    }

    @Override
    protected void setActivityView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        attachContentView($(BASE_CONTAINER_ID), layoutResID);
        setViews();
    }

    protected void setViews() {
        ViewStub toolbarStub = $(BASE_TOOLBAR_STUB_ID);
        if (toolbarStub != null) {
            toolbarStub.setLayoutResource(mUseLightToolbar ? R.layout._internal_view_toolbar_light : R.layout._internal_view_toolbar_dark);
            toolbarStub.inflate();
            View toolbarView = $(R.id._internal_toolbar);
            if (toolbarView != null) {
                setupToolbar(toolbarView);
            } else {
                throw new IllegalStateException("Can't find toolbar");
            }
        }
    }

    protected void setupToolbar(View toolbarView) {
        Toolbar toolbar = (Toolbar) toolbarView;
        toolbar.setBackgroundColor(getCompatColor(R.color.colorPrimary));

        if (getSupportActionBar() instanceof WindowDecorActionBar) {
            throw new IllegalStateException("You must make your app theme extends from " +
                    "Devbox.AppTheme or manually set windowActionBar to false.");
        }

        setSupportActionBar(toolbar);
        if (!isTaskRoot() && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * make toolbar content use Light theme, it must be called in onConfigActivity().
     */
    @BeforeConfigActivity
    protected void useLightToolbar() {
        checkConfigured();
        mUseLightToolbar = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
