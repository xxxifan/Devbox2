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

import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.xxxifan.devbox.components.uicomponent.DrawerComponent;

import static com.xxxifan.devbox.components.uicomponent.TranslucentBarComponent.FIT_TOOLBAR;

/**
 * Created by xifan on 5/16/16.
 */
public abstract class TranslucentDrawerActivity extends TranslucentActivity {

    private DrawerComponent mDrawerComponent;

    @Override
    protected void onConfigureActivity() {
        super.onConfigureActivity();
        setRootLayoutId(R.layout._internal_activity_drawer_base);
        setFitSystemWindowMode(FIT_TOOLBAR);
    }

    @Override protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        super.attachContentView(containerView, layoutResID);
        mDrawerComponent = new DrawerComponent(getDrawerView());
        addUIComponents(mDrawerComponent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && getDrawerLayout() != null) {
            getDrawerLayout().openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerComponent.getDrawerLayout();
    }

    protected abstract View getDrawerView();
}
