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

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.core.base.ToolbarActivity;
import com.xxxifan.devbox.core.util.Once;
import com.xxxifan.devbox.core.util.Strings;
import com.xxxifan.devbox.core.util.ViewUtils;
import com.xxxifan.devbox.demo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Generated for Devbox(https://github.com/xxxifan/Devbox2)
 * Date: 7/20/16 9:38 AM
 */
public class NewMainActivity extends ToolbarActivity {
    public static final String TAG = "NewMainActivity";

    @Override protected int getLayoutId() {
        return R.layout.activity_main_new;
    }

    @Override protected void onSetupActivity(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        Once.check("firstBoot", new Once.OnceCallback() {
            @Override public void onOnce() {
                ViewUtils.getSimpleDialogBuilder(getContext(), "首次启动\n\n\nPowered by Once.")
                        .build().show();
            }
        });

        System.out.println(Strings.downTimer(1473076800000L, -1) + " timer");

    }

    @Override public String getSimpleName() {
        return TAG;
    }

    @OnClick({R.id.main_drawer_activity, R.id.main_translucent_activity, R.id.main_image_translucent_activity, R.id.main_trans_drawer_activity, R.id.main_recycler_activity, R.id.main_fragment_activity})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_drawer_activity:
                MvpActivity.start(getContext());
                break;
            case R.id.main_translucent_activity:
                TestTranslucentActivity.start(getContext());
                break;
            case R.id.main_image_translucent_activity:
                TestImageTranslucent.start(getContext());
                break;
            case R.id.main_trans_drawer_activity:
                TestDrawerActivity.start(getContext());
                break;
            case R.id.main_recycler_activity:
                RecyclerActivity.start(getContext());
                break;
            case R.id.main_fragment_activity:
                TestFragmentActivity.start(getContext());
                break;
        }
    }
}
