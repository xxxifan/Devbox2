package com.xxxifan.devbox.library.base.extended;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.util.ViewUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xifan on 5/17/16.
 */
public abstract class ImageTranslucentActivity extends TranslucentActivity {

    @Override
    protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        if (containerView == null) {
            throw new IllegalStateException("Cannot find container view");
        }
        if (layoutResID == 0) {
            throw new IllegalStateException("Invalid layout id");
        }
        View contentView = getLayoutInflater().inflate(layoutResID, null, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        ((ViewGroup) containerView).addView(contentView, 0, params);

        transparentStatusBar(); // default is full transparent
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        if (isKitkat()) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbarView.getLayoutParams();
            layoutParams.topMargin = ViewUtils.getSystemBarHeight();
            // reset parent change
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.toolbar_height);
            toolbarView.setPadding(0, 0, 0, 0);
        }
    }

}
