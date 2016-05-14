package com.xxxifan.devbox.library.base;

import android.content.Context;
import android.view.View;

/**
 * Created by xifan on 15-7-22.
 * UiController, support a set of default life circle control. Use {@link BaseActivity#registerUiControllers(UiController...)} to control.
 */
public abstract class UiController {
    private View mView;

    public UiController(View view) {
        if (view == null) {
            throw new IllegalArgumentException("view cannot be null");
        }
        mView = view;
        initView(view);
    }

    public View getView() {
        return mView;
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDestroy() {
        mView = null;
    }


    protected Context getContext() {
        return mView == null ? null : mView.getContext();
    }

    protected BaseActivity getActivity() {
        return getContext() == null ? null : (BaseActivity) getContext();
    }

    protected abstract void initView(View view);
}
