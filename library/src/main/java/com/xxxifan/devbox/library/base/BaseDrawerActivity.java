package com.xxxifan.devbox.library.base;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.xxxifan.devbox.library.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xifan on 4/6/16.
 */
public abstract class BaseDrawerActivity extends BaseToolbarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void setActivityView(int layoutResID) {
        super.setContentView(R.layout._internal_activity_drawer_base);

        View containerView = $(ActivityBuilder.BASE_CONTAINER_ID);
        View drawerLayout = $(ActivityBuilder.BASE_DRAWER_ID);
        if (containerView == null) {
            throw new IllegalStateException("Cannot find base_container");
        }
        if (drawerLayout == null) {
            throw new IllegalStateException("Cannot find drawer_layout");
        }


        // setup content view
        if (layoutResID > 0) {
            attachContentView(containerView, layoutResID);
        }

        // setup drawer view
        View drawerView = getDrawerView();
        if (drawerView != null) {
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            ((DrawerLayout) drawerLayout).addView(drawerView, params);
        }

        View toolbarView = $(R.id.toolbar);
        if (toolbarView != null) {
            setupToolbar(toolbarView);
        }
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerLayout = $(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, ((Toolbar) toolbarView), 0, 0);
            mDrawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerLayout.post(new Runnable() {
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout != null) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            } else {
                super.onOptionsItemSelected(item);
            }
            return true;
        }
        return false;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected abstract View getDrawerView();
}
