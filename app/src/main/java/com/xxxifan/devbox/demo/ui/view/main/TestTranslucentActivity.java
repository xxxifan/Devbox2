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

package com.xxxifan.devbox.demo.ui.view.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xxxifan.devbox.components.TranslucentActivity;
import com.xxxifan.devbox.demo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Generated for Devbox(https://github.com/xxxifan/Devbox2)
 * Date: 7/20/16 10:26 AM
 */
public class TestTranslucentActivity extends TranslucentActivity {
    public static final String TAG = "TestTranslucentActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, TestTranslucentActivity.class);
        context.startActivity(starter);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transparent;
    }

    @Override protected void onSetupActivity(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        translucentNavBar();
    }

    @Override public String getSimpleName() {
        return TAG;
    }

    @OnClick(R.id.full_btn) public void onClick() {
        transparentStatusBar();
    }
}
