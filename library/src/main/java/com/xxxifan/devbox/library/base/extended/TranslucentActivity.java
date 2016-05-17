package com.xxxifan.devbox.library.base.extended;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xxxifan.devbox.library.util.ViewUtils;


/**
 * Created by xifan on 4/5/16.
 */
public abstract class TranslucentActivity extends ToolbarActivity {

    private boolean mFullTransparent;

    @Override
    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        super.attachContentView(containerView, layoutResID);
        setTransparentStatusBar();
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        if (isKitkat()) {
            toolbarView.setPadding(0, ViewUtils.getSystemBarHeight(), 0, 0);
            toolbarView.getLayoutParams().height += ViewUtils.getSystemBarHeight();
        }
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
