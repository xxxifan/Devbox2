package com.xxxifan.devbox.ui.main;

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.R;
import com.xxxifan.devbox.library.base.BaseFragment;

/**
 * Created by xifan on 6/8/16.
 */
public class TestFragment2 extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test2;
    }

    @Override
    protected void onSetupFragment(View view, Bundle savedInstanceState) {

    }

    @Override
    public String getSimpleName() {
        return "TestFragment2";
    }
}
