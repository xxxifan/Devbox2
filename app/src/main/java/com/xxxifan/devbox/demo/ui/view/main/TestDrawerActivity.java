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
import android.view.View;

import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.library.base.extended.TranslucentDrawerActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Generated for Devbox(https://github.com/xxxifan/Devbox2)
 * Date: 7/20/16 11:24 AM
 */
public class TestDrawerActivity extends TranslucentDrawerActivity {
    public static final String TAG = "TestDrawerActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, TestDrawerActivity.class);
        context.startActivity(starter);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transparent;
    }

    @Override protected void onSetupActivity(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override public String getSimpleName() {
        return TAG;
    }

    @Override protected View getDrawerView() {
        return View.inflate(getContext(), R.layout.view_drawer, null);
    }

    @OnClick(R.id.full_btn) public void onClick() {
        transparentStatusBar();
    }
}
