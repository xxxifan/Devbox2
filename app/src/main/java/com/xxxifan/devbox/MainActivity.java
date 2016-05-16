package com.xxxifan.devbox;

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.library.base.extended.DrawerActivity;

public class MainActivity extends DrawerActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void onSetupActivity(Bundle savedInstanceState) {

    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    protected View getDrawerView() {
        return View.inflate(getContext(), R.layout.activity_main, null);
    }
}
