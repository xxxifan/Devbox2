package com.xxxifan.devbox.demo.ui.view.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.core.base.BaseFragment;

import butterknife.ButterKnife;

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
        ButterKnife.bind(this, view);
        TextView textView = ButterKnife.findById(view, R.id.text1);
        textView.setText("Text1\n" + toString());
    }

    @Override
    public String getSimpleName() {
        return "TestFragment1";
    }
}
