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

package com.xxxifan.devbox.core.ui;

import android.support.annotation.LayoutRes;
import android.view.View;

import static com.xxxifan.devbox.core.base.uicomponent.TranslucentBarComponent.FIT_WINDOW_TOP;

/**
 * Translucent Activity for Image background. Note the layout will start from status bar,
 * you may need set a margin manually.
 */
public abstract class ImageTranslucentActivity extends TranslucentActivity {

    @Override protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        super.attachContentView(containerView, layoutResID);
        setFitSystemWindowMode(FIT_WINDOW_TOP);
        enableStatusBarHint(false);
    }
}
