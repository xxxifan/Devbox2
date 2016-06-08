package com.xxxifan.devbox.ui.main;

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.R;
import com.xxxifan.devbox.library.Devbox;
import com.xxxifan.devbox.library.base.extended.ImageTranslucentActivity;
import com.xxxifan.devbox.library.util.Fragments;

import butterknife.ButterKnife;
import butterknife.OnClick;

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
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction()
                .add(FRAGMENT_CONTAINER, new TestFragment1(), TestFragment1.TAG)
                .commitAllowingStateLoss();
    }

    @Override
    protected void setupToolbar(View toolbarView) {
        super.setupToolbar(toolbarView);
        transparentToolbar();
    }

    @OnClick(R.id.main_btn_1)
    public void onFirstClick(View view) {
        Fragments.checkout(this, new TestFragment1()).into(FRAGMENT_CONTAINER);
    }

    @OnClick(R.id.main_btn_2)
    public void onSecondClick(View view) {
        Fragments.checkout(this, new TestFragment2()).fade().into(FRAGMENT_CONTAINER);

    }

    @OnClick(R.id.main_btn_3)
    public void onThirdClick(View view) {
        Fragments.checkout(this, new TestFragment1(), "TestFragment1-2").into(FRAGMENT_CONTAINER);
    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    protected View getDrawerView() {
        return View.inflate(getContext(), R.layout.activity_main, null);
    }
}
