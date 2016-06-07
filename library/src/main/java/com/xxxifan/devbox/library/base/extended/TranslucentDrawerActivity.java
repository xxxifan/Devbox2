package com.xxxifan.devbox.library.base.extended;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.xxxifan.devbox.library.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xifan on 5/16/16.
 */
public abstract class TranslucentDrawerActivity extends TranslucentActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onConfigActivity() {
        super.onConfigActivity();
        setRootLayoutId(R.layout._internal_activity_drawer_base);
    }

    @Override
    protected void setActivityView(int layoutResID) {
        super.setContentView(layoutResID);
        attachContentView($(BASE_DRAWER_ID), layoutResID);
        setViews();
    }

    @Override
    protected void setViews() {
        View drawerLayout = $(BASE_DRAWER_ID);
        if (drawerLayout == null) {
            throw new IllegalStateException("Cannot find drawer_layout");
        }

        // setup drawer view
        View drawerView = getDrawerView();
        if (drawerView != null) {
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            ((DrawerLayout) drawerLayout).addView(drawerView, params);
        }
        super.setViews();
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerLayout = $(R.id._internal_drawer_layout);
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
        if (item.getItemId() == android.R.id.home && mDrawerLayout != null) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected abstract View getDrawerView();
}
