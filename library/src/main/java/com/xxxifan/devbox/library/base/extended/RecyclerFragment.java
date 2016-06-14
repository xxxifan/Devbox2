package com.xxxifan.devbox.library.base.extended;

import android.os.Bundle;
import android.view.View;

import com.xxxifan.devbox.library.base.BaseFragment;

/**
 * Created by xifan on 6/14/16.
 */
public class RecyclerFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void onSetupFragment(View view, Bundle savedInstanceState) {

    }

    @Override
    public String getSimpleName() {
        return null;
    }
}
