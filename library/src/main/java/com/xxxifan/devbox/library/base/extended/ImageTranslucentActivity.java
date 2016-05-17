package com.xxxifan.devbox.library.base.extended;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by xifan on 5/17/16.
 */
public abstract class ImageTranslucentActivity extends ToolbarActivity {
    private boolean mFullTransparent;

    @Override
    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        super.attachContentView(containerView, layoutResID);
        setTransparentStatusBar();
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        toolbarView.setFitsSystemWindows(true);
    }

    protected void transparentStatusBar() {
        mFullTransparent = true;
        if (isConfigured()) {
            setTransparentStatusBar();
        }
    }

    protected boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    protected boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private void setTransparentStatusBar() {
        if (isKitkat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (mFullTransparent) {
            if (isLollipop()) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }
}
