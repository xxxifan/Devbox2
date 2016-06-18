package com.xxxifan.devbox.demo.ui.view.main;

import com.xxxifan.devbox.library.base.BasePresenter;
import com.xxxifan.devbox.library.base.BaseView;

/**
 * Created by drakeet(http://drakeet.me)
 * Date: 5/18/16 3:25 PM
 */
public interface HomeMvp {
    interface View extends BaseView {
    }

    interface Presenter extends BasePresenter<View> {
    }
}