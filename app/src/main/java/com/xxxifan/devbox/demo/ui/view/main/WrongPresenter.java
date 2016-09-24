package com.xxxifan.devbox.demo.ui.view.main;

import android.content.Context;

import com.xxxifan.devbox.library.base.BasePresenter;
import com.xxxifan.devbox.library.base.BaseView;

/**
 * Created by xifan on 9/24/16.
 */

public class WrongPresenter implements BasePresenter<WrongPresenter.WrongView> {

    @Override public void setView(WrongView view) {

    }

    @Override public void onDestroy() {

    }

    public static class WrongView implements BaseView<WrongPresenter> {
        @Override public void setPresenter(WrongPresenter presenter) {

        }

        @Override public Context getContext() {
            return null;
        }

        @Override public String getSimpleName() {
            return null;
        }

        @Override public void showMessage(String msg) {

        }
    }

}
