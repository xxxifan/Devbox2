package com.xxxifan.devbox.demo.ui.view.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.library.base.BaseFragment;
import com.xxxifan.devbox.library.util.Strings;

import butterknife.ButterKnife;

/**
 * Created by xifan on 6/8/16.
 */
public class TestFragment2 extends BaseFragment {
    public static final String TAG = "TestFragment2";
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test2;
    }

    @Override
    protected void onSetupFragment(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        TextView textView = ButterKnife.findById(view, R.id.text2);
        textView.setText("Text2\n" + toString());
        if (getArguments() != null) {
            String data = getArguments().getString("data");
            if (!Strings.isEmpty(data)) {
                showMessage(data);
            }
        }
    }

    @Override
    public String getSimpleName() {
        return "TestFragment2";
    }
}
