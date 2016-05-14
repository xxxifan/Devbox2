package com.xxxifan.devbox.library.base;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xxxifan.devbox.library.R;


/**
 * Created by xifan on 4/5/16.
 */
public abstract class BaseToolbarActivity extends BaseActivity {

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
        setSupportActionBar(toolbar);
        if (!isTaskRoot() && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
