package com.xxxifan.devbox.library.base.extended;

import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.base.ActivityBuilder;
import com.xxxifan.devbox.library.base.BaseActivity;


/**
 * Created by xifan on 4/5/16.
 */
public abstract class ToolbarActivity extends BaseActivity {

    private boolean mUseLightToolbar;

    @Override
    protected void setActivityView(int layoutResID) {
        super.setContentView(R.layout._internal_activity_base);

        View containerView = findViewById(ActivityBuilder.BASE_CONTAINER_ID);
        if (containerView == null) {
            throw new IllegalStateException("Cannot find base_container");
        }

        if (layoutResID > 0) {
            attachContentView(containerView, layoutResID);
        }

        View toolbarView = $(R.id.toolbar);
        if (toolbarView != null) {
            setupToolbar(toolbarView);
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

    protected void useLightToolbar() {
        mUseLightToolbar = true;
        if (isConfigured()) {
            throw new IllegalStateException("You must call this method in onConfigActivity");
        }
    }
}
