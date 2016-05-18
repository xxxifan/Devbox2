package com.xxxifan.devbox.library.base.extended;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.base.ActivityBuilder;
import com.xxxifan.devbox.library.util.ViewUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


/**
 * Translucent status bar version of ToolbarActivity
 * add additional status bar height to toolbar and content.
 * Created by xifan on 4/5/16.
 */
public abstract class TranslucentActivity extends ToolbarActivity {

    private boolean mFullTransparent;

    @Override
    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        if (containerView == null) {
            throw new IllegalStateException("Cannot find container view");
        }
        if (layoutResID == 0) {
            throw new IllegalStateException("Invalid layout id");
        }
        View contentView = getLayoutInflater().inflate(layoutResID, null, false);
        if (containerView instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + ViewUtils.getSystemBarHeight();
            ((ViewGroup) containerView).addView(contentView, 0, params);
        } else {
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.toolbar_height) + ViewUtils.getSystemBarHeight();
            ((ViewGroup) containerView).addView(contentView, 0, params);
        }

        setTransparentStatusBar(containerView);
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
            setTransparentStatusBar($(ActivityBuilder.BASE_CONTAINER_ID));
        }
    }

    protected boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    protected boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private void setTransparentStatusBar(View containerView) {
        if (isKitkat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            ((ViewGroup.MarginLayoutParams) containerView.getLayoutParams()).topMargin += ViewUtils.getSystemBarHeight();
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
