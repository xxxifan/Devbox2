package com.xxxifan.devbox.ui.main;

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.R;
import com.xxxifan.devbox.library.base.Devbox;
import com.xxxifan.devbox.library.base.extended.ImageTranslucentActivity;

public class MainActivity extends ImageTranslucentActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onConfigActivity() {
        super.onConfigActivity();
        Devbox.init(getApplicationContext());
        transparentStatusBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onSetupActivity(Bundle savedInstanceState) {

    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        transparentToolbar();
    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    protected View getDrawerView() {
        return View.inflate(getContext(), R.layout.activity_main, null);
    }
}
