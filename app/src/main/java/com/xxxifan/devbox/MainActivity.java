package com.xxxifan.devbox;

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.library.base.Devbox;
import com.xxxifan.devbox.library.base.extended.ToolbarActivity;

public class MainActivity extends ToolbarActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onConfigActivity() {
        super.onConfigActivity();
        Devbox.init(getApplicationContext());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onSetupActivity(Bundle savedInstanceState) {

    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    protected View getDrawerView() {
        return View.inflate(getContext(), R.layout.activity_main, null);
    }
}
