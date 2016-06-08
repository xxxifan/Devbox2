package com.xxxifan.devbox.ui.main;

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.R;
import com.xxxifan.devbox.library.base.BaseFragment;

/**
 * Created by xifan on 6/8/16.
 */
public class TestFragment1 extends BaseFragment {
    public static final String TAG = "TestFragment1";
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test1;
    }

    @Override
    protected void onSetupFragment(View view, Bundle savedInstanceState) {

    }

    @Override
    public String getSimpleName() {
        return "TestFragment1";
    }
}
