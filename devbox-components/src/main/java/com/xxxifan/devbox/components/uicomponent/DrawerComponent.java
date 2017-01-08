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

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.xxxifan.devbox.components.DrawerActivity;
import com.xxxifan.devbox.core.base.BaseActivity;
import com.xxxifan.devbox.core.base.ToolbarActivity;
import com.xxxifan.devbox.core.base.uicomponent.UIComponent;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xifan on 12/21/16.
 */

public class DrawerComponent implements UIComponent {
    public static final String TAG = "DrawerComponent";

    private View mDrawerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public DrawerComponent(View drawerView) {
        this.mDrawerView = drawerView;
    }

    @Override public void inflate(View containerView) {
        mDrawerLayout = (DrawerLayout) containerView.findViewById(DrawerActivity.BASE_DRAWER_ID);
        if (mDrawerLayout == null) {
            throw new IllegalStateException("Cannot find drawer_layout");
        }

        // setup drawer view
        if (mDrawerView != null) {
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            mDrawerLayout.addView(mDrawerView, params);
        }

        BaseActivity activity = (BaseActivity) containerView.getContext();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
            Toolbar toolbar = (Toolbar) containerView.findViewById(ToolbarActivity.BASE_TOOLBAR_ID);
            if (toolbar != null) {
                mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, toolbar, 0, 0);
                mDrawerLayout.addDrawerListener(mDrawerToggle);
                mDrawerLayout.post(new Runnable() {
                    public void run() {
                        mDrawerToggle.syncState();
                    }
                });
            }
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mDrawerToggle;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override public String getTag() {
        return TAG;
    }
}
