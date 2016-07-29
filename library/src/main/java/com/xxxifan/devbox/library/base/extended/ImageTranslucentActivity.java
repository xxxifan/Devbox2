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

package com.xxxifan.devbox.library.base.extended;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import com.xxxifan.devbox.library.R;
import com.xxxifan.devbox.library.util.ViewUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Translucent Activity for Image background. Note the layout will start from status bar,
 * you may need set a margin manually.
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

        View view = getContentView();
        if (view != null) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = ViewUtils.getSystemBarHeight() + getResources()
                        .getDimensionPixelSize(R.dimen.toolbar_height);
            }
        }
        setTranslucentBar();
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        transparentToolbar();
        if (isKitkat()) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbarView.getLayoutParams();
            layoutParams.topMargin = ViewUtils.getSystemBarHeight();
        }
    }

    /**
     * @return get a real content view beside image background to let base correct layout.
     */
    protected abstract View getContentView();

}
