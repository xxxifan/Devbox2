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

import com.orhanobut.logger.Logger;
import com.xxxifan.devbox.core.base.ToolbarActivity;
import com.xxxifan.devbox.core.util.Fragments;
import com.xxxifan.devbox.demo.R;

/**
 * Generated for Devbox(https://github.com/xxxifan/Devbox2)
 * Date: 8/24/16 5:15 PM
 */
public class MvpActivity extends ToolbarActivity {
    public static final String TAG = "MvpActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, MvpActivity.class);
        context.startActivity(starter);
    }

    @Override protected int getLayoutId() {
        return R.layout.base_activity_container;
    }

    @Override protected void onSetupActivity(Bundle savedInstanceState) {
        if (savedInstanceState == null) { // create fragment instance only when savedState is null.
            TestPresenter presenter = new TestPresenter();
            presenter.setInfo("activity info"); // test a value set once by activity
            Fragments.checkout(this, new MvpFragment())
//                    .bindPresenter(new WrongPresenter()) // which will cause an Exception
                    .bindPresenter(presenter)
                    .into(FRAGMENT_CONTAINER);
        }
        Logger.e("onSetupActivity");
    }

    @Override public String getSimpleName() {
        return TAG;
    }
}
