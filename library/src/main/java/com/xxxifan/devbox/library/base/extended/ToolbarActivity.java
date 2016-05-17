package com.xxxifan.devbox.library.base.extended;

import android.support.annotation.LayoutRes;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.base.ActivityBuilder;
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
        attachContentView($(ActivityBuilder.BASE_CONTAINER_ID), layoutResID);
        setViews();
    }

    protected void setViews() {
        ViewStub toolbarStub = $(ActivityBuilder.BASE_TOOLBAR_STUB_ID);
        if (toolbarStub != null) {
            toolbarStub.setLayoutResource(mUseLightToolbar ? R.layout.view_toolbar_light : R.layout.view_toolbar_dark);
            toolbarStub.inflate();
            View toolbarView = $(R.id.toolbar);
            if (toolbarView != null) {
                setupToolbar(toolbarView);
            } else {
                throw new IllegalStateException("Can't find toolbar");
            }
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

    /**
     * make toolbar content use Light theme, it must be called in onConfigActivity().
     */
    @BeforeConfigActivity
    protected void useLightToolbar() {
        checkConfigured();
        mUseLightToolbar = true;
    }
}
