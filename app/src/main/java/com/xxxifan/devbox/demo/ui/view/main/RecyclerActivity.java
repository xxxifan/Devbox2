package com.xxxifan.devbox.demo.ui.view.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xxxifan.devbox.demo.R;
import com.xxxifan.devbox.library.base.extended.TranslucentActivity;
import com.xxxifan.devbox.library.util.Fragments;

/**
 * Generated for Devbox(https://github.com/xxxifan/Devbox2)
 * Date: 6/14/16 2:23 PM
 */
public class RecyclerActivity extends TranslucentActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, RecyclerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.base_activity_container;
    }

    @Override
    protected void onSetupActivity(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Fragments.checkout(this, new ReposFragment())
                    .into(FRAGMENT_CONTAINER);
        }
        translucentNavBar();
    }

    @Override
    public String getSimpleName() {
        return "RecyclerActivity";
    }
}
