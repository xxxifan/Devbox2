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

package com.xxxifan.devbox.core.base;

import android.support.annotation.LayoutRes;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xxxifan.devbox.core.R;
import com.xxxifan.devbox.core.base.uicomponent.ToolbarComponent;

/**
 * Created by xifan on 4/5/16.
 */
public abstract class ToolbarActivity extends BaseActivity {

    @Override
    protected void onConfigureActivity() {
        setRootLayoutId(R.layout._internal_activity_base);
    }

    @Override protected void attachContentView(View containerView, @LayoutRes int layoutResID) {
        super.attachContentView(containerView, layoutResID);
        addUIComponents(new ToolbarComponent());
    }

    /**
     * @return user defined content view attached to container.
     */
    protected View getContentView() {
        return ((ViewGroup) $(BASE_CONTAINER_ID)).getChildAt(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
