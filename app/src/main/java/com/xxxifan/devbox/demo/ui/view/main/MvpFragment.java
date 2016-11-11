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
import android.widget.Button;

import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.core.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Generated for Devbox(https://github.com/xxxifan/Devbox2)
 * Date: 8/24/16 5:15 PM
 */
public class MvpFragment extends BaseFragment implements MvpContract.View {
    public static final String TAG = "MvpFragment";
    @BindView(R.id.mvp_test_button) Button mMvpTestButton;

    private static MvpContract.Presenter mPresenter; // static presenter to keep one reference.

    @Override protected int getLayoutId() {
        return R.layout.fragment_mvp_test;
    }

    @Override protected void onSetupFragment(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) { // reset presenter's view if savedInstanceState not null.
            mPresenter.setView(this);
        }
        mMvpTestButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mPresenter.getInfo();
            }
        });
    }

    @Override public void setPresenter(MvpContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override public void onShowInfo(String info) {
        showMessage(info);
    }

    @Override public void onDestroy() {
        if (mPresenter != null) { // release the view in presenter when fragment destroy
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }

    @Override public String getSimpleName() {
        return TAG;
    }
}
